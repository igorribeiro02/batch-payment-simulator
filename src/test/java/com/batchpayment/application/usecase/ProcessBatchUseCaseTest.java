package com.batchpayment.application.usecase;

import com.batchpayment.application.port.TransactionCodeGenerator;
import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.AccountType;
import com.batchpayment.domain.model.ProcessedTransfer;
import com.batchpayment.domain.model.ProcessingType;
import com.batchpayment.domain.service.ProcessingStrategySelector;
import com.batchpayment.domain.strategy.ExternalProcessingStrategy;
import com.batchpayment.domain.strategy.InternalProcessingStrategy;
import com.batchpayment.infrastructure.persistence.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste do caso de uso SEM banco de dados real:
 * as portas recebem implementações simples (repositório em memória
 * e um gerador de códigos previsível). É o DIP facilitando o teste.
 */
class ProcessBatchUseCaseTest {

    /** Fake: gera "TX-1", "TX-2"... para os testes serem previsíveis. */
    private static class SequentialCodeGenerator implements TransactionCodeGenerator {
        private int counter = 0;

        @Override
        public String next() {
            counter++;
            return "TX-" + counter;
        }
    }

    private InMemoryAccountRepository repository;
    private ProcessBatchUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
        ProcessingStrategySelector selector = new ProcessingStrategySelector(List.of(
                new InternalProcessingStrategy(),
                new ExternalProcessingStrategy()
        ));
        useCase = new ProcessBatchUseCase(repository, new SequentialCodeGenerator(), selector);

        repository.save(account("001", AccountType.STANDARD, "100.00"));
        repository.save(account("002", AccountType.STANDARD, "100.00"));
        repository.save(account("003", AccountType.RESTRICTED, "100.00"));
    }

    private Account account(String number, AccountType type, String balance) {
        Account account = new Account(number, type);
        account.credit(new BigDecimal(balance));
        return account;
    }

    private BigDecimal balanceOf(String number) {
        return repository.findByNumber(number).orElseThrow().getBalance();
    }

    @Test
    void processesEachTransferWithTheRightStrategyAndUniqueCode() {
        BatchResult result = useCase.execute(List.of(
                new TransferRequest("001", "002", new BigDecimal("30.00")),
                new TransferRequest("003", "001", new BigDecimal("20.00"))
        ));

        assertEquals(2, result.processed().size());
        assertTrue(result.failures().isEmpty());

        ProcessedTransfer first = result.processed().get(0);
        ProcessedTransfer second = result.processed().get(1);
        assertEquals(ProcessingType.EXTERNAL, first.processingType());
        assertEquals(ProcessingType.INTERNAL, second.processingType());
        assertEquals("TX-1", first.transactionCode());
        assertEquals("TX-2", second.transactionCode());

        assertEquals(new BigDecimal("90.00"), balanceOf("001"));
        assertEquals(new BigDecimal("130.00"), balanceOf("002"));
        assertEquals(new BigDecimal("80.00"), balanceOf("003"));
    }

    @Test
    void oneFailureDoesNotStopTheRestOfTheBatch() {
        BatchResult result = useCase.execute(List.of(
                new TransferRequest("001", "002", new BigDecimal("500.00")),  // saldo insuficiente
                new TransferRequest("001", "999", new BigDecimal("10.00")),   // conta inexistente
                new TransferRequest("002", "001", new BigDecimal("10.00"))    // ok
        ));

        assertEquals(1, result.processed().size());
        assertEquals(2, result.failures().size());
        assertEquals(new BigDecimal("110.00"), balanceOf("001"));
        assertEquals(new BigDecimal("90.00"), balanceOf("002"));
    }
}
