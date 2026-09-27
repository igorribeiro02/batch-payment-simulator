package com.batchpayment.application.port;

import com.batchpayment.domain.model.Account;

import java.util.Optional;

/**
 * Porta de saída: "preciso buscar e salvar contas".
 *
 * O caso de uso depende desta INTERFACE, e não de um banco de dados.
 * Quem implementa (memória, PostgreSQL, API...) fica na infraestrutura.
 * Isto é a Inversão de Dependência (DIP).
 */
public interface AccountRepository {

    Optional<Account> findByNumber(String accountNumber);

    void save(Account account);
}
