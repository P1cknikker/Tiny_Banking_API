package org.example.repository;

import org.example.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds a customer by email address.
     * Email is unique, so at most one customer will be returned.
     *
     * @param email the email address to search for
     * @return Optional containing the customer if found, empty otherwise
     */
    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
}
