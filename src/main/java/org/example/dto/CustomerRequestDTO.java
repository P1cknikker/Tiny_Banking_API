package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(
        //Bean validation via jakartaBib
        @NotBlank String name,
        @NotBlank @Email String email
) {}