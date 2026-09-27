package com.batchpayment.domain.strategy;

import com.batchpayment.domain.exception.InsufficientBalanceException;
import com.batchpayment.domain.exception.InvalidTransferException;
import com.batchpayment.domain.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingStrategyTest {

    private Account account(String number, AccountType type, String balance) {
        Account account = new Account(number, type);
        account.credit(new BigDecimal(balance));
        return account;
    }

    @Test
    void processingGeneratesLinkedDebitAndCreditWithSameCode() {
        Account source = account("001", AccountType.STANDARD, "100.00");
        Account destination = account("002", AccountType.STANDARD, "10.00");
        Transaction tx = new Transaction("TX-1", "001", "002", new BigDecimal("40.00"));

        ProcessedTransfer result = new ExternalProcessingStrategy().process(tx, source, destination);

        assertEquals("TX-1", result.debit().transactionCode());
        assertEquals("TX-1", result.credit().transactionCode());
        assertEquals(OperationType.DEBIT, result.debit().type());
        assertEquals(OperationType.CREDIT, result.credit().type());
        assertEquals("001", result.debit().accountNumber());
        assertEquals("002", result.credit().accountNumber());
        assertEquals(new BigDecimal("60.00"), source.getBalance());
        assertEquals(new BigDecimal("50.00"), destination.getBalance());
    }

    @Test
    void externalRejectsRestrictedAccounts() {
        Account source = account("001", AccountType.RESTRICTED, "100.00");
        Account destination = account("002", AccountType.STANDARD, "10.00");
        Transaction tx = new Transaction("TX-1", "001", "002", new BigDecimal("40.00"));

        assertThrows(InvalidTransferException.class,
                () -> new ExternalProcessingStrategy().process(tx, source, destination));
        assertEquals(new BigDecimal("100.00"), source.getBalance());
    }

    @Test
    void internalAcceptsBlockedAccounts() {
        Account source = account("001", AccountType.STANDARD, "100.00");
        Account destination = account("002", AccountType.BLOCKED, "10.00");
        Transaction tx = new Transaction("TX-1", "001", "002", new BigDecimal("40.00"));

        ProcessedTransfer result = new InternalProcessingStrategy().process(tx, source, destination);

        assertEquals(ProcessingType.INTERNAL, result.processingType());
    }

    @Test
    void failedDebitLeavesBothAccountsUntouched() {
        Account source = account("001", AccountType.STANDARD, "10.00");
        Account destination = account("002", AccountType.STANDARD, "10.00");
        Transaction tx = new Transaction("TX-1", "001", "002", new BigDecimal("40.00"));

        assertThrows(InsufficientBalanceException.class,
                () -> new InternalProcessingStrategy().process(tx, source, destination));
        assertEquals(new BigDecimal("10.00"), source.getBalance());
        assertEquals(new BigDecimal("10.00"), destination.getBalance());
    }
}
