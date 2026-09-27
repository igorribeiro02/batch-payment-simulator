package com.batchpayment.application.usecase;

import com.batchpayment.domain.model.ProcessedTransfer;

import java.util.List;

/**
 * Resultado do lote: o que deu certo e o que falhou.
 *
 * Uma transferência com erro não derruba o lote inteiro:
 * ela vai para a lista de falhas e as outras continuam.
 */
public record BatchResult(
        List<ProcessedTransfer> processed,
        List<TransferFailure> failures
) {
    public BatchResult {
        processed = List.copyOf(processed);
        failures = List.copyOf(failures);
    }

    public int totalRequests() {
        return processed.size() + failures.size();
    }
}
