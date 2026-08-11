package org.example.repository;

import org.example.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT a FROM Account a JOIN FETCH a.customer WHERE a.id = :id")
    Optional<Account> findByIdWithCustomer(@Param("id") Long id);

    @Query("SELECT a FROM Account a JOIN FETCH a.customer")
    List<Account> findAllWithCustomer();

    /**
     * Finds all accounts for a specific customer.
     *
     * @param customerId the customer ID
     * @return List of accounts belonging to the customer
     */
    List<Account> findByCustomerId(Long customerId);

    boolean existsByCustomerId(Long customerId);

    long countByCustomerId(Long customerId);
}
