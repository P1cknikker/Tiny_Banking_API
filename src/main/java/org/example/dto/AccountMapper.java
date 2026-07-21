package org.example.dto;

import org.example.entity.Account;
import org.example.entity.Customer;

import java.math.BigDecimal;

public final class AccountMapper {

    private AccountMapper() {}

    public static Account toNewAccount(AccountRequestDTO dto, Customer customer) {
        Account account = new Account();
        account.setIban(dto.iban());
        account.setBalance(BigDecimal.ZERO);
        account.setCustomer(customer);
        return account;
    }
}
