package com.batchpayment.domain.strategy;

import com.batchpayment.domain.exception.InvalidTransferException;
import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.Operation;
import com.batchpayment.domain.model.ProcessedTransfer;
import com.batchpayment.domain.model.Transaction;

/**
 * Base comum das estratégias (padrão Template Method).
 *
 * O "esqueleto" do processamento é igual para todas as estratégias
 * e fica aqui, escrito uma vez só:
 *   1. conferir se as contas batem com a transação
 *   2. validação específica da estratégia (cada subclasse decide)
 *   3. debitar a origem e creditar o destino
 *   4. registrar as 2 operações com o mesmo transactionCode
 *
 * As subclasses só preenchem o que muda: o tipo e a validação específica.
 * Isto é herança usada do jeito certo: as filhas SÃO estratégias de verdade
 * e reaproveitam um algoritmo idêntico.
 */
public abstract class AbstractProcessingStrategy implements ProcessingStrategy {

    @Override
    public final ProcessedTransfer process(Transaction transaction, Account source, Account destination) {
        requireMatchingAccounts(transaction, source, destination);
        validate(source, destination);

        // O débito vem primeiro: se ele falhar (ex.: saldo insuficiente),
        // o crédito nunca acontece e nenhuma conta fica alterada.
        source.debit(transaction.amount());
        destination.credit(transaction.amount());

        String code = transaction.transactionCode();
        return new ProcessedTransfer(
                type(),
                Operation.debit(code, source.getNumber(), transaction.amount()),
                Operation.credit(code, destination.getNumber(), transaction.amount())
        );
    }

    /** Ponto de extensão: cada estratégia pode acrescentar a sua própria regra. */
    protected abstract void validate(Account source, Account destination);

    private void requireMatchingAccounts(Transaction transaction, Account source, Account destination) {
        if (!source.getNumber().equals(transaction.sourceAccountNumber())
                || !destination.getNumber().equals(transaction.destinationAccountNumber())) {
            throw new InvalidTransferException("As contas informadas não correspondem à transação");
        }
    }
}
