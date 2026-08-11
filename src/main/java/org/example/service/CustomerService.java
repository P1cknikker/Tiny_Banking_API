package org.example.service;

import org.example.dto.BankingMapper;
import org.example.dto.CustomerRequestDTO;
import org.example.dto.CustomerResponseDTO;
import org.example.entity.Customer;
import org.example.exception.CustomerHasAccountsException;
import org.example.exception.CustomerNotFoundException;
import org.example.exception.DuplicateEmailException;
import org.example.repository.AccountRepository;
import org.example.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CustomerService(CustomerRepository customerRepository,
                           AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return BankingMapper.toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAll() {
        return customerRepository.findAll().stream()
                .map(BankingMapper::toCustomerResponse)
                .toList();
    }

    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException(dto.email());
        }

        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer saved = customerRepository.save(customer);
        return BankingMapper.toCustomerResponse(saved);
    }

    @Transactional
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // E-Mail darf nur geändert werden, wenn sie nicht schon einem anderen Kunden gehört
        if (!customer.getEmail().equalsIgnoreCase(dto.email())
                && customerRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException(dto.email());
        }

        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer saved = customerRepository.save(customer);
        return BankingMapper.toCustomerResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        long accountCount = accountRepository.countByCustomerId(id);
        if (accountCount > 0) {
            throw new CustomerHasAccountsException(id, accountCount);
        }

        customerRepository.deleteById(id);
    }
}
