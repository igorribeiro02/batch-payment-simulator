package com.batchpayment.domain.model;

import com.batchpayment.domain.exception.InsufficientBalanceException;
import com.batchpayment.domain.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Testes da entidade: Java puro, sem banco, sem mocks. */
class AccountTest {

    @Test
    void newAccountStartsWithZeroBalance() {
        Account account = new Account("001", AccountType.STANDARD);

        assertEquals(0, account.getBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    void creditIncreasesBalance() {
        Account account = new Account("001", AccountType.STANDARD);

        account.credit(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

    @Test
    void debitDecreasesBalance() {
        Account account = new Account("001", AccountType.STANDARD);
        account.credit(new BigDecimal("100.00"));

        account.debit(new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), account.getBalance());
    }

    @Test
    void debitGreaterThanBalanceIsRejectedAndBalanceStaysTheSame() {
        Account account = new Account("001", AccountType.STANDARD);
        account.credit(new BigDecimal("50.00"));

        assertThrows(InsufficientBalanceException.class,
                () -> account.debit(new BigDecimal("80.00")));
        assertEquals(new BigDecimal("50.00"), account.getBalance());
    }

    @Test
    void zeroOrNegativeAmountsAreRejected() {
        Account account = new Account("001", AccountType.STANDARD);

        assertThrows(InvalidAmountException.class, () -> account.credit(BigDecimal.ZERO));
        assertThrows(InvalidAmountException.class, () -> account.credit(new BigDecimal("-10")));
        assertThrows(InvalidAmountException.class, () -> account.debit(null));
    }

    @Test
    void onlyRestrictedAndBlockedRequireInternalProcessing() {
        assertFalse(new Account("1", AccountType.STANDARD).requiresInternalProcessing());
        assertTrue(new Account("2", AccountType.RESTRICTED).requiresInternalProcessing());
        assertTrue(new Account("3", AccountType.BLOCKED).requiresInternalProcessing());
    }

    @Test
    void accountsAreEqualByNumberNotByBalance() {
        Account a = new Account("001", AccountType.STANDARD);
        Account b = new Account("001", AccountType.BLOCKED);
        Account c = new Account("002", AccountType.STANDARD);

        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}
