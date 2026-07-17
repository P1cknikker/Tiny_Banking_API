package org.example.exception;

public class DuplicateIbanException extends RuntimeException {
    public DuplicateIbanException(String iban) {
        super("IBAN already exists: " + iban);
    }
}