package com.batchpayment.domain.exception;

/** Lançada quando uma transferência referencia uma conta que não existe. */
public class AccountNotFoundException extends DomainException {

    public AccountNotFoundException(String accountNumber) {
        super("Conta não encontrada: " + accountNumber);
    }
}
