package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.*;
import org.example.entity.TransactionType;
import org.example.exception.AccountNotFoundException;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.InsufficientBalanceException;
import org.example.service.AccountService;
import org.example.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("AccountController MockMvc Tests")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @Test
    @DisplayName("GET /accounts returns 200")
    void getAllAccounts() throws Exception {
        when(accountService.getAll()).thenReturn(List.of(
                new AccountResponseDTO(1L, "DE89370400440532013000", BigDecimal.ZERO, 1L)
        ));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].iban").value("DE89370400440532013000"));
    }

    @Test
    @DisplayName("POST /accounts returns 201")
    void createAccount() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO("DE89370400440532013000", 1L);
        when(accountService.createAccount(any())).thenReturn(
                new AccountResponseDTO(1L, "DE89370400440532013000", BigDecimal.ZERO, 1L)
        );

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /accounts/{id} returns 404 when missing")
    void getAccountNotFound() throws Exception {
        when(accountService.getAccount(99L)).thenThrow(new AccountNotFoundException(99L));

        mockMvc.perform(get("/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found: 99"));
    }

    @Test
    @DisplayName("POST /accounts/{id}/deposit returns 200")
    void deposit() throws Exception {
        doNothing().when(transactionService).deposit(eq(1L), any());

        mockMvc.perform(post("/accounts/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.00}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /accounts/{id}/withdraw returns 400 on insufficient balance")
    void withdrawInsufficient() throws Exception {
        doThrow(new InsufficientBalanceException(1L, new BigDecimal("9999")))
                .when(transactionService).withdraw(eq(1L), any());

        mockMvc.perform(post("/accounts/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":9999}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /accounts/{id}/transactions returns 200")
    void getTransactions() throws Exception {
        when(transactionService.getTransactionsForAccount(1L)).thenReturn(List.of(
                new TransactionResponseDTO(1L, 1L, TransactionType.DEPOSIT,
                        new BigDecimal("100.00"), Instant.now())
        ));

        mockMvc.perform(get("/accounts/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"));
    }
}
