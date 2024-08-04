package com.acmebank.accountmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ValidateAccount {
    private Long currentAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
