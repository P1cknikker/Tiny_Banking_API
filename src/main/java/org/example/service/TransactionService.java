package org.example.service;

import org.example.dto.BankingMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void deposit(Long accountId, DepositRequestDTO dto) {
        log.info("Deposit amount={} to accountId={}", dto.amount(), accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        BigDecimal amount = dto.amount();
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(amount);
        tx.setTimestamp(Instant.now());
        transactionRepository.save(tx);
        log.info("Deposit done accountId={} newBalance={}", accountId, account.getBalance());
    }

    @Transactional
    public void withdraw(Long accountId, WithdrawRequestDTO dto) {
        log.info("Withdraw amount={} from accountId={}", dto.amount(), accountId);
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
        log.info("Withdraw done accountId={} newBalance={}", accountId, account.getBalance());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsForAccount(Long accountId) {
        log.debug("Loading transactions for account {}", accountId);
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        return transactionRepository
                .findByAccountIdOrderByTimestampDesc(accountId)
                .stream()
                .map(BankingMapper::toTransactionResponse)
                .toList();
    }
}
