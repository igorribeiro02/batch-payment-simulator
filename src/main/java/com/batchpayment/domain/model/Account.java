package com.batchpayment.domain.model;

import com.batchpayment.domain.exception.InsufficientBalanceException;
import com.batchpayment.domain.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidade Conta.
 *
 * Invariantes (regras que a conta NUNCA pode quebrar):
 *  - o número da conta nunca muda (é a identidade dela);
 *  - o saldo nunca fica negativo;
 *  - o saldo só muda por credit() e debit(), nunca por atribuição direta.
 */
public class Account {

    private final String number;
    private AccountType type;
    private BigDecimal balance;

    public Account(String number, AccountType type) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("O número da conta é obrigatório");
        }
        this.number = number;
        this.type = Objects.requireNonNull(type, "O tipo da conta é obrigatório");
        this.balance = BigDecimal.ZERO;
    }

    public void credit(BigDecimal amount) {
        requirePositive(amount);
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(number, balance, amount);
        }
        balance = balance.subtract(amount);
    }

    public void changeType(AccountType newType) {
        this.type = Objects.requireNonNull(newType, "O tipo da conta é obrigatório");
    }

    /** Regra de negócio: contas RESTRICTED ou BLOCKED só podem usar processamento INTERNAL. */
    public boolean requiresInternalProcessing() {
        return type == AccountType.RESTRICTED || type == AccountType.BLOCKED;
    }

    public String getNumber() {
        return number;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    /** Duas contas são iguais se têm o mesmo número (identidade), não o mesmo saldo. */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Account account)) return false;
        return number.equals(account.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public String toString() {
        return "Account{number=" + number + ", type=" + type + ", balance=" + balance + "}";
    }
}
