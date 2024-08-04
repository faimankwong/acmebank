package com.acmebank.accountmanager.validator

import com.acmebank.accountmanager.exception.AccountNotExistException
import com.acmebank.accountmanager.exception.InsufficientBalanceException
import com.acmebank.accountmanager.exception.TransferFailCheckingException
import com.acmebank.accountmanager.model.ValidateAccount
import com.acmebank.accountmanager.validator.AccountBalanceValidator
import com.acmebank.accountmanager.validator.AccountExistenceValidator
import com.acmebank.accountmanager.validator.AccountTransferValidator
import spock.lang.Specification
import spock.lang.Unroll

class AccountValidatorStrategySpec extends Specification {

    def accountValidationStrategy = new AccountExistenceValidator()
    def balanceValidationStrategy = new AccountBalanceValidator()
    def accountTransferValidation = new AccountTransferValidator()
    @Unroll
    def "validate account existence with fromAccountId: #fromAccountId should throw exception: #expectedException"() {
        given:
        ValidateAccount validateAccount = new ValidateAccount(currentAccountId: fromAccountId)

        when:
        accountValidationStrategy.validate(validateAccount)

        then:
        def thrownException = thrown(expectedException)
        thrownException.message == expectedMessage

        where:
        fromAccountId | expectedException        | expectedMessage
        null          | AccountNotExistException | "Invalid fromAccountId: null"
        -1            | AccountNotExistException | "Invalid fromAccountId: -1"
        0             | AccountNotExistException | "Invalid fromAccountId: 0"
    }

    @Unroll
    def "validate account existence with fromAccountId: #fromAccountId should not throw exception"() {
        given:
        ValidateAccount validateAccount = new ValidateAccount(currentAccountId: fromAccountId)

        when:
        accountValidationStrategy.validate(validateAccount)

        then:
        noExceptionThrown()

        where:
        fromAccountId = 1  // No exception expected
    }


    @Unroll
    def "validate transfer amount with amount: #amount and accountId: #currentAccountId should throw exception: #expectedException"() {
        given:
        ValidateAccount validateAccount = new ValidateAccount(amount: amount, currentAccountId: currentAccountId)

        when:
        balanceValidationStrategy.validate(validateAccount)

        then:
        def thrownException = thrown(expectedException)
        thrownException.message == expectedMessage

        where:
        amount          | currentAccountId | expectedException            | expectedMessage
        null            | 1                | InsufficientBalanceException | "Transfer amount must be greater than zero: 1"
        BigDecimal.ZERO | 1                | InsufficientBalanceException | "Transfer amount must be greater than zero: 1"
    }

    @Unroll
    def "validate transfer amount with amount: #amount and accountId: #currentAccountId should not throw exception"() {
        given:
        ValidateAccount validateAccount = new ValidateAccount(amount: amount, currentAccountId: currentAccountId)

        when:
        balanceValidationStrategy.validate(validateAccount)

        then:
        noExceptionThrown()

        where:
        amount              | currentAccountId
        BigDecimal.ONE      | 1                // No exception expected
        new BigDecimal("2") | 1                // No exception expected
    }


    @Unroll
    def "validate should throw TransferFailCheckingException for invalid fromAccountId #fromAccountId and toAccountId #toAccountId"() {
        given:
        ValidateAccount validateAccount = ValidateAccount.builder()
                .currentAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .build()

        when:
        accountTransferValidation.validate(validateAccount)

        then:
        def ex = thrown(TransferFailCheckingException)
        ex.message == expectedMessage

        where:
        fromAccountId | toAccountId || expectedMessage
        null          | 2           || "Invalid fromAccountId: null"
        -1            | 2           || "Invalid fromAccountId: -1"
        1             | null        || "Invalid toAccountId: null"
        1             | -1          || "Invalid toAccountId: -1"
    }

    def "validate should throw TransferFailCheckingException when sender and receiver are the same"() {
        given:
        Long sameAccountId = 1L
        ValidateAccount validateAccount = ValidateAccount.builder()
                .currentAccountId(sameAccountId)
                .toAccountId(sameAccountId)
                .build()

        when:
        accountTransferValidation.validate(validateAccount)

        then:
        def ex = thrown(TransferFailCheckingException)
        ex.message == "Sender and receiver cannot be the same"
    }

    def "validate should pass for valid fromAccountId and toAccountId"() {
        given:
        ValidateAccount validateAccount = ValidateAccount.builder()
                .currentAccountId(1L)
                .toAccountId(2L)
                .build()

        when:
        accountTransferValidation.validate(validateAccount)

        then:
        noExceptionThrown()
    }
}