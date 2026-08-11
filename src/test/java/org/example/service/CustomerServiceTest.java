package org.example.service;

import org.example.dto.CustomerRequestDTO;
import org.example.dto.CustomerResponseDTO;
import org.example.entity.Customer;
import org.example.exception.CustomerHasAccountsException;
import org.example.exception.CustomerNotFoundException;
import org.example.exception.DuplicateEmailException;
import org.example.repository.AccountRepository;
import org.example.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");

        testRequestDTO = new CustomerRequestDTO("John Doe", "john@example.com");
    }

    @Test
    @DisplayName("getById returns customer when found")
    void getById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        CustomerResponseDTO result = customerService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.email()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("getById throws when customer not found")
    void getById_NotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getById(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create succeeds when email is unique")
    void create_Success() {
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CustomerResponseDTO result = customerService.create(testRequestDTO);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("john@example.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("create throws DuplicateEmailException when email exists")
    void create_DuplicateEmail() {
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(testRequestDTO))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("john@example.com");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete succeeds when customer has no accounts")
    void delete_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.countByCustomerId(1L)).thenReturn(0L);

        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws when customer has accounts")
    void delete_HasAccounts() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.countByCustomerId(1L)).thenReturn(2L);

        assertThatThrownBy(() -> customerService.delete(1L))
                .isInstanceOf(CustomerHasAccountsException.class)
                .hasMessageContaining("2");

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete throws when customer not found")
    void delete_NotFound() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.delete(99L))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    @DisplayName("getAll returns list of customers")
    void getAll_Success() {
        when(customerRepository.findAll()).thenReturn(List.of(testCustomer));

        List<CustomerResponseDTO> result = customerService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("John Doe");
    }
}
