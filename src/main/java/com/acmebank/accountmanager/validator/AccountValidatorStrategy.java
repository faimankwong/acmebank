package com.acmebank.accountmanager.validator;

import com.acmebank.accountmanager.model.ValidateAccount;

public interface AccountValidatorStrategy {
    void validate(ValidateAccount validateAccount) throws RuntimeException;
}
