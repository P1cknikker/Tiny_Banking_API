package org.example.service;

import org.example.dto.CustomerRequestDTO;
import org.example.dto.CustomerResponseDTO;
import org.example.entity.Customer;
import org.example.exception.CustomerNotFoundException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für CustomerService mit Mockito
 *
 * @Mock: Erstellt ein Mock-Objekt (hier: CustomerRepository)
 * @InjectMocks: Erstellt die Klasse unter Test (CustomerService)
 *               und injiziert automatisch alle @Mock-Felder
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerRequestDTO testRequestDTO;

    /**
     * Setup vor jedem Test
     * Erstelle Test-Objekte, die du in mehreren Tests brauchst
     */
    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john@example.com");

        testRequestDTO = new CustomerRequestDTO("John Doe", "john@example.com");
    }

    // ============================================
    // Tests für: create()
    // ============================================

    @Test
    @DisplayName("Should create a customer successfully")
    void testCreateCustomer_Success() {
        // ARRANGE: Mock vorbereiten
        // Wenn repository.save() aufgerufen wird, soll es testCustomer zurückgeben
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(testCustomer);

        // ACT: Methode aufrufen
        CustomerResponseDTO result = customerService.create(testRequestDTO);

        // ASSERT: Ergebnis überprüfen
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.email()).isEqualTo("john@example.com");

        // VERIFY: Überprüfe, dass repository.save() genau einmal aufgerufen wurde
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should save customer with correct data")
    void testCreateCustomer_VerifySaveArguments() {
        // ARRANGE
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(testCustomer);

        // ACT
        customerService.create(testRequestDTO);

        // ASSERT: Überprüfe, dass der gespeicherte Customer die richtigen Werte hat
        verify(customerRepository).save(argThat(customer ->
                customer.getName().equals("John Doe") &&
                        customer.getEmail().equals("john@example.com")
        ));
    }

    // ============================================
    // Tests für: getById()
    // ============================================

    @Test
    @DisplayName("Should retrieve customer by ID successfully")
    void testGetById_Success() {
        // ARRANGE
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(testCustomer));

        // ACT
        CustomerResponseDTO result = customerService.getById(1L);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("John Doe");

        // VERIFY
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found")
    void testGetById_NotFound() {
        // ARRANGE
        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        // ACT & ASSERT: Überprüfe, dass Exception geworfen wird
        assertThatThrownBy(() -> customerService.getById(999L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");

        // VERIFY
        verify(customerRepository).findById(999L);
    }

    // ============================================
    // Tests für: update()
    // ============================================

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomer_Success() {
        // ARRANGE
        CustomerRequestDTO updateDTO = new CustomerRequestDTO("Jane Doe", "jane@example.com");
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("Jane Doe");
        updatedCustomer.setEmail("jane@example.com");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(updatedCustomer);

        // ACT
        CustomerResponseDTO result = customerService.update(1L, updateDTO);

        // ASSERT
        assertThat(result.name()).isEqualTo("Jane Doe");
        assertThat(result.email()).isEqualTo("jane@example.com");

        // VERIFY: findById und save wurden aufgerufen
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when updating non-existent customer")
    void testUpdateCustomer_NotFound() {
        // ARRANGE
        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        CustomerRequestDTO updateDTO = new CustomerRequestDTO("Jane", "jane@example.com");

        // ACT & ASSERT
        assertThatThrownBy(() -> customerService.update(999L, updateDTO))
                .isInstanceOf(CustomerNotFoundException.class);

        // VERIFY: save sollte NICHT aufgerufen werden (da Exception vorher geworfen wird)
        verify(customerRepository, never()).save(any());
    }

    // ============================================
    // Tests für: delete()
    // ============================================

    @Test
    @DisplayName("Should delete customer successfully")
    void testDeleteCustomer_Success() {
        // ARRANGE
        when(customerRepository.existsById(1L))
                .thenReturn(true);

        // ACT
        customerService.delete(1L);

        // ASSERT & VERIFY: deleteById sollte aufgerufen worden sein
        verify(customerRepository).existsById(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when deleting non-existent customer")
    void testDeleteCustomer_NotFound() {
        // ARRANGE
        when(customerRepository.existsById(999L))
                .thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> customerService.delete(999L))
                .isInstanceOf(CustomerNotFoundException.class);

        // VERIFY: deleteById sollte NICHT aufgerufen werden
        verify(customerRepository, never()).deleteById(any());
    }

    // ============================================
    // Tests für: getAll()
    // ============================================

    @Test
    @DisplayName("Should retrieve all customers")
    void testGetAllCustomers_Success() {
        // ARRANGE
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Jane Doe");
        customer2.setEmail("jane@example.com");

        when(customerRepository.findAll())
                .thenReturn(List.of(testCustomer, customer2));

        // ACT
        List<CustomerResponseDTO> result = customerService.getAll();

        // ASSERT
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(CustomerResponseDTO::name)
                .containsExactly("John Doe", "Jane Doe");

        // VERIFY
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void testGetAllCustomers_Empty() {
        // ARRANGE
        when(customerRepository.findAll())
                .thenReturn(List.of());

        // ACT
        List<CustomerResponseDTO> result = customerService.getAll();

        // ASSERT
        assertThat(result).isEmpty();

        // VERIFY
        verify(customerRepository).findAll();
    }
}
