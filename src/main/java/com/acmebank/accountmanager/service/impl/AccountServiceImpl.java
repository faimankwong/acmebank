package com.acmebank.accountmanager.service.impl;

import com.acmebank.accountmanager.exception.AccountNotExistException;
import com.acmebank.accountmanager.exception.InsufficientBalanceException;
import com.acmebank.accountmanager.model.Account;
import com.acmebank.accountmanager.model.ValidateAccount;
import com.acmebank.accountmanager.model.request.TransferRequest;
import com.acmebank.accountmanager.model.response.BalanceResponse;
import com.acmebank.accountmanager.model.response.TransferResponse;
import com.acmebank.accountmanager.repository.AccountRepository;
import com.acmebank.accountmanager.service.AccountService;
import com.acmebank.accountmanager.validator.AccountValidatorStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public BalanceResponse getBalance(Long accountId, List<AccountValidatorStrategy> validationStrategies) throws AccountNotExistException {
        ValidateAccount validateAccount = ValidateAccount.builder().currentAccountId(accountId).build();
        for (AccountValidatorStrategy strategy : validationStrategies) {
            strategy.validate(validateAccount);
        }
        Account currentAccount = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotExistException("Account not found accountId: " + accountId));
        return BalanceResponse.builder().accountId(currentAccount.getId()).currency(currentAccount.getCurrency()).balance(currentAccount.getBalance()).build();
    }

    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest transferRequest, List<AccountValidatorStrategy> validationStrategies) throws AccountNotExistException {
        Long fromAccountId = transferRequest.getFromAccountId();
        Long toAccountId = transferRequest.getToAccountId();
        BigDecimal amount = transferRequest.getAmount();
        ValidateAccount validateAccount = ValidateAccount.builder().currentAccountId(fromAccountId).toAccountId(toAccountId).amount(amount).build();
        for (AccountValidatorStrategy strategy : validationStrategies) {
            strategy.validate(validateAccount);
        }

        Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow(() -> new AccountNotExistException("Account not found fromAccountId: " + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId).orElseThrow(() -> new AccountNotExistException("Account not found toAccountId: " + toAccountId));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds in account: " + fromAccountId);
        }
        // Perform the transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        // Save both accounts in a single operation
        accountRepository.saveAll(List.of(fromAccount, toAccount));
        return TransferResponse.builder().message(String.format("Transfer of %s from account %d to account %d was successful.", amount, fromAccountId, toAccountId)).status("success").build();
    }

}
