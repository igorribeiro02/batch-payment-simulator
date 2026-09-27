package com.batchpayment.domain.strategy;

import com.batchpayment.domain.model.Account;
import com.batchpayment.domain.model.ProcessedTransfer;
import com.batchpayment.domain.model.ProcessingType;
import com.batchpayment.domain.model.Transaction;

/**
 * Contrato de toda estratégia de processamento.
 *
 * Quem usa uma estratégia só conhece esta interface:
 * não sabe (nem precisa saber) se ela é INTERNAL ou EXTERNAL.
 */
public interface ProcessingStrategy {

    ProcessingType type();

    ProcessedTransfer process(Transaction transaction, Account source, Account destination);
}
