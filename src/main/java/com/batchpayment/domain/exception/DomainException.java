package com.batchpayment.domain.exception;

/**
 * Exceção base para qualquer regra de negócio violada.
 *
 * Ter uma classe-mãe permite que camadas de fora capturem
 * "qualquer erro de regra de negócio" com um único catch,
 * sem confundir com erros técnicos (banco fora do ar, etc.).
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
