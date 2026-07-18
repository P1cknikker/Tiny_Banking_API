package org.example.dto;

import java.math.BigDecimal;

public record AccountResponseDTO(
        Long id,
        String iban,
        BigDecimal balance,
        Long customerId
) {}
