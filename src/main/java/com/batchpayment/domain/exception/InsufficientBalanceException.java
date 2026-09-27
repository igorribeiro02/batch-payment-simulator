package com.batchpayment.domain.exception;

import java.math.BigDecimal;

/** Lançada quando um débito deixaria a conta com saldo negativo. */
public class InsufficientBalanceException extends DomainException {

    public InsufficientBalanceException(String accountNumber, BigDecimal balance, BigDecimal amount) {
        super("Saldo insuficiente na conta " + accountNumber
                + ". Saldo: " + balance + ", débito solicitado: " + amount);
    }
}
