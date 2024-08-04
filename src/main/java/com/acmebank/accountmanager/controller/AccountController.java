package com.acmebank.accountmanager.controller;

import com.acmebank.accountmanager.model.request.TransferRequest;
import com.acmebank.accountmanager.model.response.BalanceResponse;
import com.acmebank.accountmanager.model.response.TransferResponse;
import com.acmebank.accountmanager.service.AccountService;
import com.acmebank.accountmanager.validator.AccountBalanceValidator;
import com.acmebank.accountmanager.validator.AccountExistenceValidator;
import com.acmebank.accountmanager.validator.AccountTransferValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;

@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountExistenceValidator existenceValidation;
    private final AccountBalanceValidator balanceValidation;
    private final AccountTransferValidator accountTransferValidation;

    @PostMapping(value = "/transfer", consumes = "application/json")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(this.accountService.transfer(transferRequest, Arrays.asList(this.balanceValidation, this.accountTransferValidation)));
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> balance(@RequestParam long accountId) {
        return ResponseEntity.ok(this.accountService.getBalance(accountId, Collections.singletonList(this.existenceValidation)));
    }
}