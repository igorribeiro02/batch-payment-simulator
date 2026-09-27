package com.batchpayment.application.usecase;

import com.batchpayment.application.port.AccountRepository;
import com.batchpayment.application.port.TransactionCodeGenerator;
import com.batchpayment.domain.exception.AccountNotFoundException;
import com.batchpayment.domain.exception.DomainException;
import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.ProcessedTransfer;
import com.batchpayment.domain.model.Transaction;
import com.batchpayment.domain.service.ProcessingStrategySelector;
import com.batchpayment.domain.strategy.ProcessingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso: processar um lote de transferências.
 *
 * Ele ORQUESTRA; não contém regra de negócio. Para cada pedido:
 *   1. gera o transactionCode
 *   2. cria a Transaction (que se valida sozinha)
 *   3. busca as duas contas
 *   4. pede ao seletor a estratégia certa
 *   5. manda a estratégia processar (débito + crédito)
 *   6. salva as contas alteradas
 *
 * Repare nas dependências: só interfaces (portas) e classes do domínio.
 * Nenhuma menção a banco de dados, framework ou UUID.
 */
public class ProcessBatchUseCase {

    private final AccountRepository accountRepository;
    private final TransactionCodeGenerator codeGenerator;
    private final ProcessingStrategySelector strategySelector;

    public ProcessBatchUseCase(AccountRepository accountRepository,
                               TransactionCodeGenerator codeGenerator,
                               ProcessingStrategySelector strategySelector) {
        this.accountRepository = accountRepository;
        this.codeGenerator = codeGenerator;
        this.strategySelector = strategySelector;
    }

    public BatchResult execute(List<TransferRequest> requests) {
        List<ProcessedTransfer> processed = new ArrayList<>();
        List<TransferFailure> failures = new ArrayList<>();

        for (TransferRequest request : requests) {
            try {
                processed.add(processOne(request));
            } catch (DomainException error) {
                failures.add(new TransferFailure(request, error.getMessage()));
            }
        }
        return new BatchResult(processed, failures);
    }

    private ProcessedTransfer processOne(TransferRequest request) {
        Transaction transaction = new Transaction(
                codeGenerator.next(),
                request.sourceAccountNumber(),
                request.destinationAccountNumber(),
                request.amount()
        );

        Account source = findAccount(transaction.sourceAccountNumber());
        Account destination = findAccount(transaction.destinationAccountNumber());

        ProcessingStrategy strategy = strategySelector.select(source, destination);
        ProcessedTransfer result = strategy.process(transaction, source, destination);

        accountRepository.save(source);
        accountRepository.save(destination);
        return result;
    }

    private Account findAccount(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
