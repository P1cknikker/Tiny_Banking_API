package org.example.dto;

import org.example.entity.Customer;

public final class CustomerMapper {

    private CustomerMapper() {}

    public static void apply(CustomerRequestDTO dto, Customer customer) {
        customer.setName(dto.name());
        customer.setEmail(dto.email());
    }
}
