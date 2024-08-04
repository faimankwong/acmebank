package com.acmebank.accountmanager.validator;

import com.acmebank.accountmanager.exception.AccountNotExistException;
import com.acmebank.accountmanager.model.ValidateAccount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountExistenceValidator implements AccountValidatorStrategy {

    @Override
    public void validate(ValidateAccount validateAccount) throws AccountNotExistException {
        Long fromAccountId = validateAccount.getCurrentAccountId();
        if (fromAccountId == null || fromAccountId < 1) {
            throw new AccountNotExistException("Invalid fromAccountId: " + fromAccountId);
        }
    }

}
