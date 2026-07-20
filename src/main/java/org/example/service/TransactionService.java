package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.DepositRequestDTO;
import org.example.dto.TransactionResponseDTO;
import org.example.dto.WithdrawRequestDTO;
import org.example.entity.Account;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.exception.AccountNotFoundException;
import org.example.exception.InsufficientBalanceException;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void deposit(Long accountId, DepositRequestDTO dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        BigDecimal amount = dto.amount();
        // Betrag > 0 ist idealerweise via Bean Validation erledigt.

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account); // meist optional, aber für Anfänger oft klar

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setTimestamp(Instant.now());

        transactionRepository.save(tx);
    }

    @Transactional
    public void withdraw(Long accountId, WithdrawRequestDTO dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        BigDecimal amount = dto.amount();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(accountId, amount);
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(TransactionType.WITHDRAW);
        tx.setAmount(amount);
        tx.setTimestamp(Instant.now());

        transactionRepository.save(tx);
    }

    public List<TransactionResponseDTO> getTransactionsForAccount(Long accountId) {
        // optional: Account existieren prüfen; oder direkt TransactionRepository abfragen
        // Mapping später
        List<Transaction> txs = transactionRepository
                .findByAccountIdOrderByTimestampDesc(accountId);

        return txs.stream().map(this::mapTransactionToResponse).toList();
    }
}
