package org.example.repository;

import org.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds an account by IBAN.
     * IBAN is unique, so at most one account will be returned.
     *
     * @param iban the IBAN to search for
     * @return Optional containing the account if found, empty otherwise
     */
    Optional<Account> findByIban(String iban);

    /**
     * Finds all accounts for a specific customer.
     *
     * @param customerId the customer ID
     * @return List of accounts belonging to the customer
     */
    List<Account> findByCustomerId(Long customerId);
}
