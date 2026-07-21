package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.CustomerRequestDTO;
import org.example.dto.CustomerResponseDTO;
import org.example.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * GET /customers
     * Alle Kunden abrufen
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        log.info("Fetching all customers");
        List<CustomerResponseDTO> customers = customerService.getAll();
        return ResponseEntity.ok(customers);
    }

    /**
     * POST /customers
     * Neuen Kunden anlegen
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        log.info("Creating customer with name: {}", requestDTO.name());
        CustomerResponseDTO createdCustomer = customerService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    /**
     * GET /customers/{id}
     * Einen Kunden nach ID abrufen
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        log.info("Fetching customer with id: {}", id);
        CustomerResponseDTO customer = customerService.getById(id);
        return ResponseEntity.ok(customer);
    }

    /**
     * PUT /customers/{id}
     * Einen Kunden aktualisieren
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        log.info("Updating customer with id: {}", id);
        CustomerResponseDTO updatedCustomer = customerService.update(id, requestDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * DELETE /customers/{id}
     * Einen Kunden löschen
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with id: {}", id);
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
