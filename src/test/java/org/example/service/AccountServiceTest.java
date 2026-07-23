package org.example.service;

import org.example.dto.AccountRequestDTO;
import org.example.dto.AccountResponseDTO;
import org.example.dto.BankingMapper;
import org.example.entity.Account;
import org.example.entity.Customer;
import org.example.exception.AccountNotFoundException;
import org.example.exception.CustomerNotFoundException;
import org.example.exception.DuplicateIbanException;
import org.example.repository.AccountRepository;
import org.example.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer testCustomer;
    private Account testAccount;
    private AccountRequestDTO accountRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");

        // Setup test account
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setIban("DE89370400440532013000");
        testAccount.setBalance(BigDecimal.valueOf(1000.00));
        testAccount.setCustomer(testCustomer);

        // Setup request DTO
        accountRequestDTO = new AccountRequestDTO("DE89370400440532013000", 1L);
    }

    // ==================== createAccount Tests ====================

    @Test
    @DisplayName("createAccount should create account successfully when customer exists and IBAN is unique")
    void testCreateAccountSuccess() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByIban("DE89370400440532013000")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        AccountResponseDTO result = accountService.createAccount(accountRequestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.iban()).isEqualTo("DE89370400440532013000");
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(1000.00));
        verify(customerRepository).findById(1L);
        verify(accountRepository).findByIban("DE89370400440532013000");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("createAccount should throw CustomerNotFoundException when customer does not exist")
    void testCreateAccountCustomerNotFound() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.createAccount(accountRequestDTO))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("1");

        verify(customerRepository).findById(1L);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("createAccount should throw DuplicateIbanException when IBAN already exists")
    void testCreateAccountDuplicateIban() {
        // Arrange
        Account existingAccount = new Account();
        existingAccount.setId(2L);
        existingAccount.setIban("DE89370400440532013000");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByIban("DE89370400440532013000"))
                .thenReturn(Optional.of(existingAccount));

        // Act & Assert
        assertThatThrownBy(() -> accountService.createAccount(accountRequestDTO))
                .isInstanceOf(DuplicateIbanException.class)
                .hasMessageContaining("DE89370400440532013000");

        verify(customerRepository).findById(1L);
        verify(accountRepository).findByIban("DE89370400440532013000");
        verify(accountRepository, never()).save(any());
    }

    // ==================== getAccount Tests ====================

    @Test
    @DisplayName("getAccount should return account when it exists")
    void testGetAccountSuccess() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        AccountResponseDTO result = accountService.getAccount(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.iban()).isEqualTo("DE89370400440532013000");
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(1000.00));
        verify(accountRepository).findById(1L);
    }

    @Test
    @DisplayName("getAccount should throw AccountNotFoundException when account does not exist")
    void testGetAccountNotFound() {
        // Arrange
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accountService.getAccount(999L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("999");

        verify(accountRepository).findById(999L);
    }

    // ==================== getAll Tests ====================

    @Test
    @DisplayName("getAll should return list of all accounts")
    void testGetAllSuccess() {
        // Arrange
        Account account2 = new Account();
        account2.setId(2L);
        account2.setIban("DE89370400440532013001");
        account2.setBalance(BigDecimal.valueOf(500.00));
        account2.setCustomer(testCustomer);

        List<Account> accounts = List.of(testAccount, account2);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<AccountResponseDTO> result = accountService.getAll();

        // Assert
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);
        verify(accountRepository).findAll();
    }

    @Test
    @DisplayName("getAll should return empty list when no accounts exist")
    void testGetAllEmpty() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(List.of());

        // Act
        List<AccountResponseDTO> result = accountService.getAll();

        // Assert
        assertThat(result).isNotNull().isEmpty();
        verify(accountRepository).findAll();
    }

    @Test
    @DisplayName("getAll should handle multiple accounts correctly")
    void testGetAllMultipleAccounts() {
        // Arrange
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Jane Doe");
        customer2.setEmail("jane@example.com");

        Account account2 = new Account();
        account2.setId(2L);
        account2.setIban("DE89370400440532013001");
        account2.setBalance(BigDecimal.valueOf(2000.00));
        account2.setCustomer(customer2);

        Account account3 = new Account();
        account3.setId(3L);
        account3.setIban("DE89370400440532013002");
        account3.setBalance(BigDecimal.valueOf(1500.00));
        account3.setCustomer(testCustomer);

        List<Account> accounts = List.of(testAccount, account2, account3);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<AccountResponseDTO> result = accountService.getAll();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).extracting("id").containsExactly(1L, 2L, 3L);
        assertThat(result).extracting("iban").containsExactly(
                "DE89370400440532013000",
                "DE89370400440532013001",
                "DE89370400440532013002"
        );
        verify(accountRepository).findAll();
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("createAccount should initialize balance to zero")
    void testCreateAccountInitializeBalanceToZero() {
        // Arrange
        Account newAccount = new Account();
        newAccount.setId(2L);
        newAccount.setIban("DE89370400440532013001");
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCustomer(testCustomer);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByIban("DE89370400440532013001")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        // Act
        AccountResponseDTO result = accountService.createAccount(
                new AccountRequestDTO("DE89370400440532013001", 1L)
        );

        // Assert
        assertThat(result.balance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("getAll should preserve account order")
    void testGetAllPreservesOrder() {
        // Arrange
        Account account1 = new Account();
        account1.setId(1L);
        account1.setIban("IBAN001");

        Account account2 = new Account();
        account2.setId(2L);
        account2.setIban("IBAN002");

        Account account3 = new Account();
        account3.setId(3L);
        account3.setIban("IBAN003");

        List<Account> accounts = List.of(account1, account2, account3);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<AccountResponseDTO> result = accountService.getAll();

        // Assert
        assertThat(result)
                .extracting("iban")
                .containsExactly("IBAN001", "IBAN002", "IBAN003");
    }
}
