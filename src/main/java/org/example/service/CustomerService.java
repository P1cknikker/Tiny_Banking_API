package org.example.service;

import org.example.dto.*;
import org.example.entity.Customer;
import org.example.exception.CustomerNotFoundException;
import org.example.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer saved = customerRepository.save(customer);

        return BankingMapper.toCustomerResponse(saved);
    }
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer saved = customerRepository.save(customer);

        return BankingMapper.toCustomerResponse(saved);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }

    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(BankingMapper::toCustomerResponse)
                .toList();
    }

}
