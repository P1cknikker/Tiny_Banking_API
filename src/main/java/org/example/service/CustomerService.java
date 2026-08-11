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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CustomerService(CustomerRepository customerRepository,
                           AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO getById(Long id) {
        log.debug("Loading customer {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return BankingMapper.toCustomerResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAll() {
        log.debug("Loading all customers");
        return customerRepository.findAll().stream()
                .map(BankingMapper::toCustomerResponse)
                .toList();
    }

    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        log.info("Creating customer name={}, email={}", dto.name(), dto.email());
        if (customerRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException(dto.email());
        }

        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer saved = customerRepository.save(customer);
        log.info("Created customer id={}", saved.getId());
        return BankingMapper.toCustomerResponse(saved);
    }

    @Transactional
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        log.info("Updating customer id={}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (!customer.getEmail().equalsIgnoreCase(dto.email())
                && customerRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException(dto.email());
        }

        customer.setName(dto.name());
        customer.setEmail(dto.email());

        Customer saved = customerRepository.save(customer);
        log.info("Updated customer id={}", saved.getId());
        return BankingMapper.toCustomerResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting customer id={}", id);
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        long accountCount = accountRepository.countByCustomerId(id);
        if (accountCount > 0) {
            throw new CustomerHasAccountsException(id, accountCount);
        }

        customerRepository.deleteById(id);
        log.info("Deleted customer id={}", id);
    }
}
