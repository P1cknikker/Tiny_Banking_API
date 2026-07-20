package org.example.service;

import org.example.dto.AccountRequestDTO;
import org.example.dto.AccountResponseDTO;
import org.example.entity.Account;
import org.example.entity.Customer;
import org.example.exception.AccountNotFoundException;
import org.example.exception.CustomerNotFoundException;
import org.example.exception.DuplicateIbanException;
import org.example.repository.AccountRepository;
import org.example.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public AccountResponseDTO createAccount(AccountRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.customerId()));

        accountRepository.findByIban(dto.iban()).ifPresent(existing -> {
            throw new DuplicateIbanException(dto.iban());
        });

        Account account = new Account();
        account.setIban(dto.iban());
        account.setBalance(BigDecimal.ZERO);
        account.setCustomer(customer);

        Account saved = accountRepository.save(account);

        // TODO: Mapping später
        return mapAccountToResponse(saved);
    }

    public AccountResponseDTO getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        // Achtung: hier müsstest du ggf. AccountNotFoundException nutzen – kommt auf deine Exception-Logik an.
        // Ziel: "Account nicht gefunden" => AccountNotFoundException (404)
        return mapAccountToResponse(account);
    }
}
