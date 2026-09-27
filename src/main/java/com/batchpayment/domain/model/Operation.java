package com.batchpayment.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Uma operação já efetuada sobre uma conta: um DÉBITO ou um CRÉDITO.
 *
 * É um record: imutável por natureza. Depois de criada,
 * uma operação é um fato registrado e não pode ser alterada.
 */
public record Operation(
        String transactionCode,
        String accountNumber,
        OperationType type,
        BigDecimal amount
) {
    public Operation {
        Objects.requireNonNull(transactionCode, "transactionCode é obrigatório");
        Objects.requireNonNull(accountNumber, "accountNumber é obrigatório");
        Objects.requireNonNull(type, "type é obrigatório");
        Objects.requireNonNull(amount, "amount é obrigatório");
    }

    public static Operation debit(String transactionCode, String accountNumber, BigDecimal amount) {
        return new Operation(transactionCode, accountNumber, OperationType.DEBIT, amount);
    }

    public static Operation credit(String transactionCode, String accountNumber, BigDecimal amount) {
        return new Operation(transactionCode, accountNumber, OperationType.CREDIT, amount);
    }
}
