package com.batchpayment.application.usecase;

/** Uma transferência do lote que falhou, com o motivo. */
public record TransferFailure(
        TransferRequest request,
        String reason
) {
}
