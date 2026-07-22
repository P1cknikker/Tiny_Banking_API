package org.example.service;

import org.example.dto.DepositRequestDTO;
import org.example.dto.WithdrawRequestDTO;
import org.example.entity.Account;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setBalance(new BigDecimal("1000.00"));
    }

    // ============================================
    // Tests für: deposit()
    // ============================================

    @Test
    @DisplayName("Should deposit money successfully")
    void testDeposit_Success() {
        // ARRANGE
        DepositRequestDTO depositDTO = new DepositRequestDTO(new BigDecimal("100.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT
        transactionService.deposit(1L, depositDTO);

        // ASSERT
        assertThat(testAccount.getBalance())
                .isEqualTo(new BigDecimal("1100.00"));

        // VERIFY: Account und Transaction wurden gespeichert
        verify(accountRepository).save(testAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException on deposit")
    void testDeposit_AccountNotFound() {
        // ARRANGE
        DepositRequestDTO depositDTO = new DepositRequestDTO(new BigDecimal("100.00"));
        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> transactionService.deposit(999L, depositDTO))
                .isInstanceOf(AccountNotFoundException.class);

        // VERIFY: Keine Operationen sollten stattgefunden haben
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create Transaction with correct type and amount")
    void testDeposit_VerifyTransaction() {
        // ARRANGE
        DepositRequestDTO depositDTO = new DepositRequestDTO(new BigDecimal("50.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT
        transactionService.deposit(1L, depositDTO);

        // ASSERT: Überprüfe, dass Transaction mit richtigen Werten erstellt wurde
        verify(transactionRepository).save(argThat(tx ->
                tx.getType() == TransactionType.DEPOSIT &&
                        tx.getAmount().equals(new BigDecimal("50.00")) &&
                        tx.getAccount().getId() == 1L
        ));
    }

    // ============================================
    // Tests für: withdraw()
    // ============================================

    @Test
    @DisplayName("Should withdraw money successfully")
    void testWithdraw_Success() {
        // ARRANGE
        WithdrawRequestDTO withdrawDTO = new WithdrawRequestDTO(new BigDecimal("100.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT
        transactionService.withdraw(1L, withdrawDTO);

        // ASSERT
        assertThat(testAccount.getBalance())
                .isEqualTo(new BigDecimal("900.00"));

        // VERIFY
        verify(accountRepository).save(testAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException on withdraw")
    void testWithdraw_AccountNotFound() {
        // ARRANGE
        WithdrawRequestDTO withdrawDTO = new WithdrawRequestDTO(new BigDecimal("100.00"));
        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> transactionService.withdraw(999L, withdrawDTO))
                .isInstanceOf(AccountNotFoundException.class);

        // VERIFY: Keine Operationen sollten stattgefunden haben
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException on withdraw with insufficient funds")
    void testWithdraw_InsufficientBalance() {
        // ARRANGE
        WithdrawRequestDTO withdrawDTO = new WithdrawRequestDTO(new BigDecimal("2000.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT & ASSERT
        assertThatThrownBy(() -> transactionService.withdraw(1L, withdrawDTO))
                .isInstanceOf(InsufficientBalanceException.class);

        // VERIFY: Balance sollte nicht geändert werden
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create Transaction with correct type and amount on withdraw")
    void testWithdraw_VerifyTransaction() {
        // ARRANGE
        WithdrawRequestDTO withdrawDTO = new WithdrawRequestDTO(new BigDecimal("250.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT
        transactionService.withdraw(1L, withdrawDTO);

        // ASSERT: Überprüfe, dass Transaction mit richtigen Werten erstellt wurde
        verify(transactionRepository).save(argThat(tx ->
                tx.getType() == TransactionType.WITHDRAW &&
                        tx.getAmount().equals(new BigDecimal("250.00")) &&
                        tx.getAccount().getId() == 1L
        ));
    }

    // ============================================
    // Edge Cases
    // ============================================

    @Test
    @DisplayName("Should withdraw exact amount leaving zero balance")
    void testWithdraw_ExactAmount() {
        // ARRANGE
        WithdrawRequestDTO withdrawDTO = new WithdrawRequestDTO(new BigDecimal("1000.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT
        transactionService.withdraw(1L, withdrawDTO);

        // ASSERT
        assertThat(testAccount.getBalance())
                .isEqualTo(BigDecimal.ZERO);

        verify(accountRepository).save(testAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should deposit multiple times correctly")
    void testDeposit_Multiple() {
        // ARRANGE
        DepositRequestDTO depositDTO1 = new DepositRequestDTO(new BigDecimal("100.00"));
        DepositRequestDTO depositDTO2 = new DepositRequestDTO(new BigDecimal("200.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT
        transactionService.deposit(1L, depositDTO1);
        transactionService.deposit(1L, depositDTO2);

        // ASSERT
        assertThat(testAccount.getBalance())
                .isEqualTo(new BigDecimal("1300.00"));

        // VERIFY: save() should be called 4 times (2 deposits x (account + transaction))
        verify(accountRepository, times(2)).save(testAccount);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should handle deposit and withdraw sequence correctly")
    void testDeposit_ThenWithdraw() {
        // ARRANGE
        DepositRequestDTO depositDTO = new DepositRequestDTO(new BigDecimal("500.00"));
        WithdrawRequestDTO withdrawDTO = new WithdrawRequestDTO(new BigDecimal("300.00"));
        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        // ACT
        transactionService.deposit(1L, depositDTO);
        transactionService.withdraw(1L, withdrawDTO);

        // ASSERT
        assertThat(testAccount.getBalance())
                .isEqualTo(new BigDecimal("1200.00"));

        verify(accountRepository, times(2)).save(testAccount);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

}
