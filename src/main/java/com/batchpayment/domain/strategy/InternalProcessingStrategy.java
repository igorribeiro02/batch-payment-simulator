package com.batchpayment.domain.strategy;

import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.ProcessingType;

/**
 * Processamento INTERNAL: aceita qualquer tipo de conta.
 * É o caminho obrigatório para contas RESTRICTED ou BLOCKED.
 */
public class InternalProcessingStrategy extends AbstractProcessingStrategy {

    @Override
    public ProcessingType type() {
        return ProcessingType.INTERNAL;
    }

    @Override
    protected void validate(Account source, Account destination) {
        // Nenhuma restrição extra: o processamento interno atende todas as contas.
    }
}
