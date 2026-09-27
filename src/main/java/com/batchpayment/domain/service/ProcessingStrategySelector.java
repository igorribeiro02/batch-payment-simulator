package com.batchpayment.domain.service;

import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.ProcessingType;
import com.batchpayment.domain.strategy.ProcessingStrategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de domínio que aplica a regra de escolha da estratégia:
 *  - se a origem OU o destino for RESTRICTED/BLOCKED -> INTERNAL
 *  - se as duas contas forem STANDARD                -> EXTERNAL
 *
 * Decisão de projeto: uma transferência envolve duas contas, então basta
 * UMA delas ser restrita para o processamento ser INTERNAL.
 *
 * O seletor recebe as estratégias prontas (não dá "new" nelas).
 * Assim, adicionar uma estratégia nova não exige mexer aqui dentro.
 */
public class ProcessingStrategySelector {

    private final Map<ProcessingType, ProcessingStrategy> strategies = new EnumMap<>(ProcessingType.class);

    public ProcessingStrategySelector(List<ProcessingStrategy> availableStrategies) {
        for (ProcessingStrategy strategy : availableStrategies) {
            strategies.put(strategy.type(), strategy);
        }
        for (ProcessingType type : ProcessingType.values()) {
            if (!strategies.containsKey(type)) {
                throw new IllegalArgumentException("Nenhuma estratégia registrada para " + type);
            }
        }
    }

    public ProcessingStrategy select(Account source, Account destination) {
        boolean requiresInternal = source.requiresInternalProcessing()
                || destination.requiresInternalProcessing();

        ProcessingType type = requiresInternal ? ProcessingType.INTERNAL : ProcessingType.EXTERNAL;
        return strategies.get(type);
    }
}
