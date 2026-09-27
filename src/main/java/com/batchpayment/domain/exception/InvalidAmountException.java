package com.batchpayment.domain.exception;

import java.math.BigDecimal;

/** Lançada quando um valor monetário é nulo, zero ou negativo. */
public class InvalidAmountException extends DomainException {

    public InvalidAmountException(BigDecimal amount) {
        super("O valor deve ser maior que zero. Recebido: " + amount);
    }
}
