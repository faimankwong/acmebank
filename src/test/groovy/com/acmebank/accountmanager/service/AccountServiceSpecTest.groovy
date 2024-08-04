import com.acmebank.accountmanager.exception.AccountNotExistException
import com.acmebank.accountmanager.exception.InsufficientBalanceException
import com.acmebank.accountmanager.model.Account
import com.acmebank.accountmanager.model.request.TransferRequest
import com.acmebank.accountmanager.repository.AccountRepository
import com.acmebank.accountmanager.service.impl.AccountServiceImpl
import com.acmebank.accountmanager.validator.AccountValidatorStrategy
import spock.lang.Specification
import spock.lang.Unroll

class AccountServiceSpecTest extends Specification {

    private AccountRepository accountRepository = Mock()

    def accountService = new AccountServiceImpl(accountRepository)


    def "getBalance should return the correct balance"() {
        given: "An account with a specific balance"
        Long accountId = 1L
        BigDecimal balance = new BigDecimal("1000.00")
        String currency = "HKD"
        Account account = new Account(id: accountId, balance: balance, currency: currency)
        List<AccountValidatorStrategy> validationStrategies = []

        accountRepository.findById(accountId) >> Optional.of(account)

        when: "getBalance is called"
        def result = accountService.getBalance(accountId, validationStrategies)

        then: "The correct balance is returned"
        result.balance == balance
        result.currency == currency
        result.accountId == 1L
    }

    def "getBalance should throw AccountNotExistException if account does not exist"() {
        given: "An invalid account ID"
        Long accountId = 1L
        List<AccountValidatorStrategy> validationStrategies = []

        accountRepository.findById(accountId) >> Optional.empty()

        when: "getBalance is called"
        accountService.getBalance(accountId, validationStrategies)

        then: "AccountNotExistException is thrown"
        thrown(AccountNotExistException)
    }

    @Unroll
    def "transfer should successfully transfer amount from #fromAccountId to #toAccountId"() {
        given: "Two accounts with specific balances"
        Account fromAccount = new Account(id: fromAccountId, balance: fromBalance)
        Account toAccount = new Account(id: toAccountId, balance: toBalance)
        List<AccountValidatorStrategy> validationStrategies = []

        accountRepository.findById(fromAccountId) >> Optional.of(fromAccount)
        accountRepository.findById(toAccountId) >> Optional.of(toAccount)

        when: "transfer is called"
        boolean result = accountService.transfer(new TransferRequest(fromAccountId, toAccountId, amount), validationStrategies)

        then: "The transfer is successful and balances are updated"
        result
        fromAccount.balance == expectedFromBalance
        toAccount.balance == expectedToBalance

        where:
        fromAccountId | toAccountId | fromBalance | toBalance | amount | expectedFromBalance | expectedToBalance
        1L            | 2L          | 1000.00     | 500.00    | 200.00 | 800.00              | 700.00
        3L            | 4L          | 1500.00     | 300.00    | 500.00 | 1000.00             | 800.00
    }

    def "transfer should throw AccountNotExistException if fromAccount does not exist"() {
        given: "An invalid fromAccount ID"
        Long fromAccountId = 1L
        Long toAccountId = 2L
        BigDecimal amount = new BigDecimal("200.00")
        List<AccountValidatorStrategy> validationStrategies = []

        accountRepository.findById(fromAccountId) >> Optional.empty()

        when: "transfer is called"
        accountService.transfer(new TransferRequest(fromAccountId, toAccountId, amount), validationStrategies)

        then: "AccountNotExistException is thrown"
        thrown(AccountNotExistException)
    }

    def "transfer should throw AccountNotExistException if toAccount does not exist"() {
        given: "An invalid toAccount ID"
        Long fromAccountId = 1L
        Long toAccountId = 2L
        BigDecimal amount = new BigDecimal("200.00")
        Account fromAccount = new Account(id: fromAccountId, balance: new BigDecimal("1000.00"))
        List<AccountValidatorStrategy> validationStrategies = []

        accountRepository.findById(fromAccountId) >> Optional.of(fromAccount)
        accountRepository.findById(toAccountId) >> Optional.empty()

        when: "transfer is called"
        accountService.transfer(new TransferRequest(fromAccountId, toAccountId, amount), validationStrategies)

        then: "AccountNotExistException is thrown"
        thrown(AccountNotExistException)
    }

    def "transfer should throw InsufficientBalanceException if fromAccount has insufficient funds"() {
        given: "An account with insufficient balance"
        Long fromAccountId = 1L
        Long toAccountId = 2L
        BigDecimal amount = new BigDecimal("2000.00")
        Account fromAccount = new Account(id: fromAccountId, balance: new BigDecimal("1000.00"))
        Account toAccount = new Account(id: toAccountId, balance: new BigDecimal("500.00"))
        List<AccountValidatorStrategy> validationStrategies = []

        accountRepository.findById(fromAccountId) >> Optional.of(fromAccount)
        accountRepository.findById(toAccountId) >> Optional.of(toAccount)

        when: "transfer is called"
        accountService.transfer(new TransferRequest(fromAccountId, toAccountId, amount), validationStrategies)

        then: "InsufficientBalanceException is thrown"
        thrown(InsufficientBalanceException)
    }
}
