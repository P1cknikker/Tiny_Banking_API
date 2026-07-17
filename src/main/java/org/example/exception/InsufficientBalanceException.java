package org.example.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long accountId, java.math.BigDecimal amount) {
        super("Insufficient balance for account " + accountId + ": amount=" + amount);
    }
}