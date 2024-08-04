package com.acmebank.accountmanager.service;

import com.acmebank.accountmanager.exception.AccountNotExistException;
import com.acmebank.accountmanager.model.request.TransferRequest;
import com.acmebank.accountmanager.model.response.BalanceResponse;
import com.acmebank.accountmanager.model.response.TransferResponse;
import com.acmebank.accountmanager.validator.AccountValidatorStrategy;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {


    BalanceResponse getBalance(Long accountId, List<AccountValidatorStrategy> validationStrategies) throws AccountNotExistException;

    TransferResponse transfer(TransferRequest transferRequest, List<AccountValidatorStrategy> validationStrategies) throws AccountNotExistException;
}
