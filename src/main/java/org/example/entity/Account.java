package org.example.entity;

public class Account {
    int id;
    char iban;
    int balance;
    int customerId;

    public Account(int id, char iban, int balance, int customerId ){
        this.id = id;
        this.iban = iban;
        this.balance = balance;
        this.customerId = customerId;
    }
}
