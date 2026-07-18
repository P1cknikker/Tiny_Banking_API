package org.example.dto;

import org.example.entity.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponseDTO(
        Long id,
        Long accountId,
        TransactionType type,
        BigDecimal amount,
        Instant timestamp
) {}
