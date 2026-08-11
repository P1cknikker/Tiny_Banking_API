package org.example.service;

import org.example.dto.DepositRequestDTO;
import org.example.dto.TransactionResponseDTO;
import org.example.dto.WithdrawRequestDTO;
import org.example.entity.Account;
import org.example.entity.Customer;
import org.example.entity.Transaction;
import org.example.entity.TransactionType;
import org.example.exception.AccountNotFoundException;
import org.example.exception.InsufficientBalanceException;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setId(1L);

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setCustomer(customer);
        testAccount.setIban("DE89370400440532013000");
    }

    @Test
    @DisplayName("deposit increases balance and saves transaction")
    void deposit_Success() {
        DepositRequestDTO dto = new DepositRequestDTO(new BigDecimal("100.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        transactionService.deposit(1L, dto);

        assertThat(testAccount.getBalance()).isEqualByComparingTo(new BigDecimal("1100.00"));
        verify(accountRepository).save(testAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("deposit throws when account not found")
    void deposit_AccountNotFound() {
        DepositRequestDTO dto = new DepositRequestDTO(new BigDecimal("100.00"));
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deposit(999L, dto))
                .isInstanceOf(AccountNotFoundException.class);

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("withdraw decreases balance when sufficient funds")
    void withdraw_Success() {
        WithdrawRequestDTO dto = new WithdrawRequestDTO(new BigDecimal("200.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        transactionService.withdraw(1L, dto);

        assertThat(testAccount.getBalance()).isEqualByComparingTo(new BigDecimal("800.00"));
        verify(accountRepository).save(testAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("withdraw throws InsufficientBalanceException")
    void withdraw_InsufficientBalance() {
        WithdrawRequestDTO dto = new WithdrawRequestDTO(new BigDecimal("2000.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> transactionService.withdraw(1L, dto))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("withdraw throws when account not found")
    void withdraw_AccountNotFound() {
        WithdrawRequestDTO dto = new WithdrawRequestDTO(new BigDecimal("50.00"));
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.withdraw(999L, dto))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    @DisplayName("getTransactionsForAccount returns transactions when account exists")
    void getTransactions_Success() {
        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setAccount(testAccount);
        tx.setType(TransactionType.DEPOSIT);
        tx.setAmount(new BigDecimal("100.00"));
        tx.setTimestamp(Instant.now());

        when(accountRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findByAccountIdOrderByTimestampDesc(1L))
                .thenReturn(List.of(tx));

        List<TransactionResponseDTO> result = transactionService.getTransactionsForAccount(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(result.get(0).amount()).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("getTransactionsForAccount throws when account not found")
    void getTransactions_AccountNotFound() {
        when(accountRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> transactionService.getTransactionsForAccount(999L))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
