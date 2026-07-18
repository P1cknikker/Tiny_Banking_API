package org.example.dto;

public record CustomerResponseDTO(
        Long id,
        String name,
        String email
) {}