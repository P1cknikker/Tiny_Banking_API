package org.example.dto;

import org.example.entity.Account;
import org.example.entity.Customer;
import org.example.entity.Transaction;

public final class BankingMapper {
    private BankingMapper() {}

    //CUSTOMER
    public static CustomerResponseDTO toCustomerResponse(Customer c){
        return new CustomerResponseDTO(
                c.getId(),
                c.getName(),
                c.getEmail()
        );
    }

    //ACCOUNT
    public static AccountResponseDTO toAccountResponse(Account a){
        return new AccountResponseDTO(
                a.getId(),
                a.getIban(),
                a.getBalance(),
                a.getCustomer().getId()
        );
    }

    //TRANSACTION
    public static TransactionResponseDTO toTransactionResponse(Transaction t){
        return new TransactionResponseDTO(
                t.getId(),
                t.getAccount().getId(),
                t.getType(),
                t.getAmount(),
                t.getTimestamp()
        );
    }
}
