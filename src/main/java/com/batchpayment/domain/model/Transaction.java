package com.batchpayment.domain.model;

import com.batchpayment.domain.exception.InvalidAmountException;
import com.batchpayment.domain.exception.InvalidTransferException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Uma transferência que ainda VAI ser processada:
 * "mover {amount} da conta {sourceAccountNumber} para {destinationAccountNumber}".
 *
 * O construtor valida tudo. Se um objeto Transaction existe, ele é válido.
 */
public record Transaction(
        String transactionCode,
        String sourceAccountNumber,
        String destinationAccountNumber,
        BigDecimal amount
) {
    public Transaction {
        Objects.requireNonNull(transactionCode, "transactionCode é obrigatório");
        Objects.requireNonNull(sourceAccountNumber, "conta de origem é obrigatória");
        Objects.requireNonNull(destinationAccountNumber, "conta de destino é obrigatória");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new InvalidTransferException("A conta de origem e a de destino devem ser diferentes");
        }
    }
}
