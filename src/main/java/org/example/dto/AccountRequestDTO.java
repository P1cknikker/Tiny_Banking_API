package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AccountRequestDTO(
        @NotBlank String iban,
        @NotNull @Positive Long customerId
) {}
