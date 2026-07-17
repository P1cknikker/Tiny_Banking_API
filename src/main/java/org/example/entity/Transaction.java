package org.example.entity;

public class Transaction {
    //Declaration
    int id;
    int accountId;
    String type;
    int amount;
    int timestamp;

    //Constructor + Variable initialization
    public Transaction(int id, int accountId, String type, int amount, int timestamp){
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp =timestamp;
    }
}
