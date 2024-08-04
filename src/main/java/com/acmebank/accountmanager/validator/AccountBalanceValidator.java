package com.acmebank.accountmanager.validator;

import com.acmebank.accountmanager.exception.AccountNotExistException;
import com.acmebank.accountmanager.exception.InsufficientBalanceException;
import com.acmebank.accountmanager.model.ValidateAccount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class AccountBalanceValidator implements AccountValidatorStrategy {
    @Override
    public void validate(ValidateAccount validateAccount) throws AccountNotExistException {
        BigDecimal amount = validateAccount.getAmount();
        Long currentAccountId = validateAccount.getCurrentAccountId();
        if (amount == null || amount.compareTo(BigDecimal.ONE) < 0) {
            throw new InsufficientBalanceException("Transfer amount must be greater than zero: " + currentAccountId);
        }
    }
}
