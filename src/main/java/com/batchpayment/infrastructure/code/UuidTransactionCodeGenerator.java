package com.batchpayment.infrastructure.code;

import com.batchpayment.application.port.TransactionCodeGenerator;

import java.util.UUID;

/** Gera códigos de transação únicos usando UUID. */
public class UuidTransactionCodeGenerator implements TransactionCodeGenerator {

    @Override
    public String next() {
        return UUID.randomUUID().toString();
    }
}
