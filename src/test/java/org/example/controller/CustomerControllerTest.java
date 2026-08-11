package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.AccountResponseDTO;
import org.example.dto.CustomerRequestDTO;
import org.example.dto.CustomerResponseDTO;
import org.example.exception.CustomerNotFoundException;
import org.example.exception.GlobalExceptionHandler;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("CustomerController MockMvc Tests")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private AccountService accountService;

    @Test
    @DisplayName("GET /customers returns 200")
    void getAllCustomers() throws Exception {
        when(customerService.getAll()).thenReturn(List.of(
                new CustomerResponseDTO(1L, "Max", "max@example.com")
        ));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Max"));
    }

    @Test
    @DisplayName("POST /customers returns 201")
    void createCustomer() throws Exception {
        CustomerRequestDTO request = new CustomerRequestDTO("Anna", "anna@test.de");
        when(customerService.create(any())).thenReturn(
                new CustomerResponseDTO(1L, "Anna", "anna@test.de")
        );

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("anna@test.de"));
    }

    @Test
    @DisplayName("GET /customers/{id} returns 404 when missing")
    void getCustomerNotFound() throws Exception {
        when(customerService.getById(99L)).thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(get("/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: 99"));
    }

    @Test
    @DisplayName("PUT /customers/{id} returns 200")
    void updateCustomer() throws Exception {
        CustomerRequestDTO request = new CustomerRequestDTO("Max Updated", "max@example.com");
        when(customerService.update(eq(1L), any())).thenReturn(
                new CustomerResponseDTO(1L, "Max Updated", "max@example.com")
        );

        mockMvc.perform(put("/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max Updated"));
    }

    @Test
    @DisplayName("DELETE /customers/{id} returns 204")
    void deleteCustomer() throws Exception {
        doNothing().when(customerService).delete(1L);

        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /customers/{id}/accounts returns 200")
    void getAccountsByCustomer() throws Exception {
        when(accountService.getAllByCustomerId(1L)).thenReturn(List.of(
                new AccountResponseDTO(1L, "DE89370400440532013000", BigDecimal.TEN, 1L)
        ));

        mockMvc.perform(get("/customers/1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].iban").value("DE89370400440532013000"));
    }

    @Test
    @DisplayName("POST /customers with invalid body returns 400")
    void createCustomerValidationError() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest());
    }
}
