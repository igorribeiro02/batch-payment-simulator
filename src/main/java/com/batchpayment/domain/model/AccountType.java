package com.batchpayment.domain.model;

/**
 * Tipos possíveis de conta.
 *
 * Um enum garante que só esses três valores existem:
 * não há como criar um tipo "BLOKED" por erro de digitação.
 */
public enum AccountType {
    STANDARD,
    RESTRICTED,
    BLOCKED
}
