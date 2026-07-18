package org.example.entity;

import java.math.BigDecimal;

public class Account {
    //Declaration
    Long id;
    String iban;
    BigDecimal balance;
    Long customerId;

    //Constructor + Variable initialization
    public Account(Long id, String iban, BigDecimal balance, Long customerId ){
        this.id = id;
        this.iban = iban;
        this.balance = balance;
        this.customerId = customerId;
    }

    public int deposit(BigDecimal amount){
        balance += amount;
        return balance;
    }
    public int withdraw(BigDecimal amount){
        if (balance - amount < 0) {
            throw new IndexOutOfBoundsException("nei mou chin aar!");
        }
        balance -= amount;
        return balance;
    }
}
