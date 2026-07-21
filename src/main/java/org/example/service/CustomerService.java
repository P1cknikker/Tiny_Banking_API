package org.example.service;

import org.example.dto.BankingMapper;
import org.example.dto.CustomerResponseDTO;
import org.example.entity.Customer;
import org.example.exception.CustomerNotFoundException;
import org.example.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return BankingMapper.toCustomerResponse(customer);
    }

    // create/update/delete analog
}
