package com.batchpayment.domain.model;

import java.util.Objects;

/**
 * O resultado de uma transferência processada:
 * as 2 operações vinculadas (débito e crédito) e a estratégia usada.
 *
 * O construtor garante a regra de negócio central:
 * débito e crédito com o MESMO transactionCode.
 */
public record ProcessedTransfer(
        ProcessingType processingType,
        Operation debit,
        Operation credit
) {
    public ProcessedTransfer {
        Objects.requireNonNull(processingType, "processingType é obrigatório");
        Objects.requireNonNull(debit, "débito é obrigatório");
        Objects.requireNonNull(credit, "crédito é obrigatório");
        if (debit.type() != OperationType.DEBIT || credit.type() != OperationType.CREDIT) {
            throw new IllegalArgumentException("Uma transferência precisa de um DÉBITO e um CRÉDITO");
        }
        if (!debit.transactionCode().equals(credit.transactionCode())) {
            throw new IllegalArgumentException("Débito e crédito precisam do mesmo transactionCode");
        }
    }

    public String transactionCode() {
        return debit.transactionCode();
    }
}
