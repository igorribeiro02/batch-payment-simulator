package com.batchpayment.infrastructure.persistence;

import com.batchpayment.application.port.AccountRepository;
import com.batchpayment.domain.model.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementação da porta AccountRepository guardando as contas em memória.
 *
 * Para trocar por um banco de verdade, basta criar outra classe que
 * implemente AccountRepository. O caso de uso e o domínio não mudam uma linha.
 */
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accounts = new HashMap<>();

    @Override
    public Optional<Account> findByNumber(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    @Override
    public void save(Account account) {
        accounts.put(account.getNumber(), account);
    }
}
