package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.*;
import org.example.service.AccountService;
import org.example.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    /**
     * GET /accounts
     * Alle Konten abrufen
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        log.info("Fetching all accounts");
        List<AccountResponseDTO> accounts = accountService.getAll();
        return ResponseEntity.ok(accounts);
    }

    /**
     * POST /accounts
     * Neues Konto anlegen
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountRequestDTO requestDTO) {
        log.info("Creating account for customer id: {} with IBAN: {}",
                requestDTO.customerId(), requestDTO.iban());
        AccountResponseDTO createdAccount = accountService.createAccount(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    /**
     * GET /accounts/{id}
     * Ein Konto nach ID abrufen
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable Long id) {
        log.info("Fetching account with id: {}", id);
        AccountResponseDTO account = accountService.getAccount(id);
        return ResponseEntity.ok(account);
    }

    /**
     * POST /accounts/{id}/deposit
     * Geld auf Konto einzahlen
     */
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Void> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequestDTO requestDTO) {
        log.info("Depositing {} to account {}", requestDTO.amount(), id);
        transactionService.deposit(id, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * POST /accounts/{id}/withdraw
     * Geld von Konto abheben
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable Long id,
            @Valid @RequestBody WithdrawRequestDTO requestDTO) {
        log.info("Withdrawing {} from account {}", requestDTO.amount(), id);
        transactionService.withdraw(id, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /accounts/{id}/transactions
     * Alle Transaktionen eines Kontos abrufen
     */
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(@PathVariable Long id) {
        log.info("Fetching transactions for account {}", id);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsForAccount(id);
        return ResponseEntity.ok(transactions);
    }
}
