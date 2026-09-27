package com.batchpayment.application.usecase;

import java.math.BigDecimal;

/**
 * Dado de ENTRADA do caso de uso: um pedido de transferência como chega de fora
 * (de um arquivo, de uma API...). Ainda não foi validado nem tem transactionCode.
 */
public record TransferRequest(
        String sourceAccountNumber,
        String destinationAccountNumber,
        BigDecimal amount
) {
}
