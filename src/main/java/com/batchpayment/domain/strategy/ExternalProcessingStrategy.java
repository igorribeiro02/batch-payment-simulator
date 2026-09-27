package com.batchpayment.domain.strategy;

import com.batchpayment.domain.exception.InvalidTransferException;
import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.ProcessingType;

/**
 * Processamento EXTERNAL: só para contas STANDARD.
 *
 * A validação é uma defesa extra: mesmo que alguém chame esta estratégia
 * diretamente, sem passar pelo seletor, a regra de negócio é respeitada.
 */
public class ExternalProcessingStrategy extends AbstractProcessingStrategy {

    @Override
    public ProcessingType type() {
        return ProcessingType.EXTERNAL;
    }

    @Override
    protected void validate(Account source, Account destination) {
        if (source.requiresInternalProcessing() || destination.requiresInternalProcessing()) {
            throw new InvalidTransferException(
                    "Contas RESTRICTED ou BLOCKED não podem usar processamento EXTERNAL");
        }
    }
}
