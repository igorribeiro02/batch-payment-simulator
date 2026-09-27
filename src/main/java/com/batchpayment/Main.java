package com.batchpayment;

import com.batchpayment.application.usecase.BatchResult;
import com.batchpayment.application.usecase.ProcessBatchUseCase;
import com.batchpayment.application.usecase.TransferFailure;
import com.batchpayment.application.usecase.TransferRequest;
import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.AccountType;
import com.batchpayment.domain.model.ProcessedTransfer;
import com.batchpayment.domain.service.ProcessingStrategySelector;
import com.batchpayment.domain.strategy.ExternalProcessingStrategy;
import com.batchpayment.domain.strategy.InternalProcessingStrategy;
import com.batchpayment.infrastructure.code.UuidTransactionCodeGenerator;
import com.batchpayment.infrastructure.persistence.InMemoryAccountRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Composition Root: o ÚNICO lugar que conhece as classes concretas
 * e "monta" o sistema, ligando cada interface à sua implementação.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Infraestrutura
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        UuidTransactionCodeGenerator codeGenerator = new UuidTransactionCodeGenerator();

        // 2. Domínio
        ProcessingStrategySelector selector = new ProcessingStrategySelector(List.of(
                new InternalProcessingStrategy(),
                new ExternalProcessingStrategy()
        ));

        // 3. Caso de uso, recebendo tudo pelo construtor
        ProcessBatchUseCase processBatch = new ProcessBatchUseCase(repository, codeGenerator, selector);

        // Dados de exemplo
        repository.save(accountWithBalance("001", AccountType.STANDARD, "1000.00"));
        repository.save(accountWithBalance("002", AccountType.STANDARD, "500.00"));
        repository.save(accountWithBalance("003", AccountType.RESTRICTED, "300.00"));
        repository.save(accountWithBalance("004", AccountType.BLOCKED, "0.00"));

        List<TransferRequest> batch = List.of(
                new TransferRequest("001", "002", new BigDecimal("150.00")),  // STANDARD -> STANDARD: EXTERNAL
                new TransferRequest("004", "001", new BigDecimal("10.00")),   // falha: saldo insuficiente (004 tem 0)
                new TransferRequest("003", "001", new BigDecimal("100.00")),  // RESTRICTED envolvida: INTERNAL
                new TransferRequest("002", "004", new BigDecimal("50.00")),   // BLOCKED envolvida: INTERNAL
                new TransferRequest("001", "999", new BigDecimal("10.00")),   // falha: conta inexistente
                new TransferRequest("001", "002", new BigDecimal("-5.00"))    // falha: valor inválido
        );

        BatchResult result = processBatch.execute(batch);
        printResult(result, repository);
    }

    private static Account accountWithBalance(String number, AccountType type, String initialBalance) {
        Account account = new Account(number, type);
        BigDecimal amount = new BigDecimal(initialBalance);
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            account.credit(amount);
        }
        return account;
    }

    private static void printResult(BatchResult result, InMemoryAccountRepository repository) {
        System.out.println("=== Lote processado: " + result.totalRequests() + " pedidos ===\n");

        System.out.println("Sucessos (" + result.processed().size() + "):");
        for (ProcessedTransfer transfer : result.processed()) {
            System.out.printf("  [%s] %s%n", transfer.processingType(), transfer.transactionCode());
            System.out.printf("     DEBIT  conta %s  %s%n", transfer.debit().accountNumber(), transfer.debit().amount());
            System.out.printf("     CREDIT conta %s  %s%n", transfer.credit().accountNumber(), transfer.credit().amount());
        }

        System.out.println("\nFalhas (" + result.failures().size() + "):");
        for (TransferFailure failure : result.failures()) {
            System.out.printf("  %s -> %s: %s%n",
                    failure.request().sourceAccountNumber(),
                    failure.request().destinationAccountNumber(),
                    failure.reason());
        }

        System.out.println("\nSaldos finais:");
        for (String number : List.of("001", "002", "003", "004")) {
            repository.findByNumber(number).ifPresent(account -> System.out.println("  " + account));
        }
    }
}
