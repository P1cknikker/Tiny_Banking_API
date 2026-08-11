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
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setIban("DE89370400440532013000");
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setCustomer(testCustomer);

        accountRequestDTO = new AccountRequestDTO("DE89370400440532013000", 1L);
    }

    @Test
    @DisplayName("createAccount succeeds when customer exists and IBAN is unique")
    void createAccount_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByIban("DE89370400440532013000")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AccountResponseDTO result = accountService.createAccount(accountRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.iban()).isEqualTo("DE89370400440532013000");
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.customerId()).isEqualTo(1L);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("createAccount throws CustomerNotFoundException")
    void createAccount_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createAccount(accountRequestDTO))
                .isInstanceOf(CustomerNotFoundException.class);

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("createAccount throws DuplicateIbanException")
    void createAccount_DuplicateIban() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByIban("DE89370400440532013000"))
                .thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> accountService.createAccount(accountRequestDTO))
                .isInstanceOf(DuplicateIbanException.class);

        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAccount returns account when found")
    void getAccount_Success() {
        when(accountRepository.findByIdWithCustomer(1L)).thenReturn(Optional.of(testAccount));

        AccountResponseDTO result = accountService.getAccount(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.iban()).isEqualTo("DE89370400440532013000");
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("getAccount throws when not found")
    void getAccount_NotFound() {
        when(accountRepository.findByIdWithCustomer(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount(999L))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    @DisplayName("getAll returns all accounts")
    void getAll_Success() {
        when(accountRepository.findAllWithCustomer()).thenReturn(List.of(testAccount));

        List<AccountResponseDTO> result = accountService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).iban()).isEqualTo("DE89370400440532013000");
    }
}
