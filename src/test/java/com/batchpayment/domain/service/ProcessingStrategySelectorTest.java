package com.batchpayment.domain.service;

import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.AccountType;
import com.batchpayment.domain.model.ProcessingType;
import com.batchpayment.domain.strategy.ExternalProcessingStrategy;
import com.batchpayment.domain.strategy.InternalProcessingStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProcessingStrategySelectorTest {

    private final ProcessingStrategySelector selector = new ProcessingStrategySelector(List.of(
            new InternalProcessingStrategy(),
            new ExternalProcessingStrategy()
    ));

    private ProcessingType selectedFor(AccountType sourceType, AccountType destinationType) {
        return selector.select(new Account("1", sourceType), new Account("2", destinationType)).type();
    }

    @Test
    void standardToStandardUsesExternal() {
        assertEquals(ProcessingType.EXTERNAL, selectedFor(AccountType.STANDARD, AccountType.STANDARD));
    }

    @Test
    void anyRestrictedOrBlockedAccountUsesInternal() {
        assertEquals(ProcessingType.INTERNAL, selectedFor(AccountType.RESTRICTED, AccountType.STANDARD));
        assertEquals(ProcessingType.INTERNAL, selectedFor(AccountType.STANDARD, AccountType.BLOCKED));
        assertEquals(ProcessingType.INTERNAL, selectedFor(AccountType.BLOCKED, AccountType.RESTRICTED));
    }

    @Test
    void selectorRequiresAStrategyForEveryType() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProcessingStrategySelector(List.of(new InternalProcessingStrategy())));
    }
}
