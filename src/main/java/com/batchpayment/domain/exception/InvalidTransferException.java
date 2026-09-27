package com.batchpayment.domain.exception;

/** Lançada quando uma regra de transferência é violada (ex.: origem igual ao destino). */
public class InvalidTransferException extends DomainException {

    public InvalidTransferException(String message) {
        super(message);
    }
}
