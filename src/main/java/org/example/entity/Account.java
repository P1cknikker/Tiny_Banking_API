package org.example.entity;

public class Account {
    //Declaration
    int id;
    char iban;
    int balance;
    int customerId;

    //Constructor + Variable initialization
    public Account(int id, char iban, int balance, int customerId ){
        this.id = id;
        this.iban = iban;
        this.balance = balance;
        this.customerId = customerId;
    }

    public int deposit(int amount){
        balance += amount;
        return balance;
    }
    public int withdraw(int amount){
        if (balance - amount < 0) {
            throw new IndexOutOfBoundsException("nei mou chin aar!");
        }
        balance -= amount;
        return balance;
    }
}
