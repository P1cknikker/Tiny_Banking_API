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

    public BigDecimal deposit(BigDecimal amount){
        return balance.add(amount);
    }
    public BigDecimal withdraw(BigDecimal amount){
        if ((balance.subtract(amount)).compareTo(BigDecimal.ZERO) < 0) {
            throw new IndexOutOfBoundsException("nei mou chin aar!");
        }
        return balance.subtract(amount);
    }
}
