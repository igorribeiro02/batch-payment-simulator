package com.batchpayment.application.port;

/**
 * Porta de saída: "preciso de um código único para cada transação".
 *
 * Esconder a geração atrás de uma interface permite que os testes
 * usem códigos previsíveis ("TX-1", "TX-2") em vez de UUIDs aleatórios.
 */
public interface TransactionCodeGenerator {

    String next();
}
