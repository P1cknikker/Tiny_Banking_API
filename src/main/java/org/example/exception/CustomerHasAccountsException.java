package org.example.exception;

public class CustomerHasAccountsException extends RuntimeException {

    public CustomerHasAccountsException(Long customerId, long accountCount) {
        super("Cannot delete customer " + customerId + ": still has " + accountCount + " account(s)");
    }
}
