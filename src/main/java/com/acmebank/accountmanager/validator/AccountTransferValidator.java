package com.acmebank.accountmanager.validator;

import com.acmebank.accountmanager.exception.AccountNotExistException;
import com.acmebank.accountmanager.exception.TransferFailCheckingException;
import com.acmebank.accountmanager.model.ValidateAccount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountTransferValidator implements AccountValidatorStrategy {
    @Override
    public void validate(ValidateAccount validateAccount) throws AccountNotExistException {
        Long fromAccountId = validateAccount.getCurrentAccountId();
        Long toAccountId = validateAccount.getToAccountId();
        if (fromAccountId == null || fromAccountId < 1) {
            throw new TransferFailCheckingException("Invalid fromAccountId: " + fromAccountId);
        }
        if (toAccountId == null || toAccountId < 1) {
            throw new TransferFailCheckingException("Invalid toAccountId: " + toAccountId);
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new TransferFailCheckingException("Sender and receiver cannot be the same");
        }
    }
}
