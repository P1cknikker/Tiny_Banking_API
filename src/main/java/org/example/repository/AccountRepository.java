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

    Optional<Account> findByIban(String iban);

    @Query("SELECT a FROM Account a JOIN FETCH a.customer WHERE a.id = :id")
    Optional<Account> findByIdWithCustomer(@Param("id") Long id);

    @Query("SELECT a FROM Account a JOIN FETCH a.customer")
    List<Account> findAllWithCustomer();

    @Query("SELECT a FROM Account a JOIN FETCH a.customer WHERE a.customer.id = :customerId")
    List<Account> findByCustomerId(@Param("customerId") Long customerId);

    boolean existsByCustomerId(Long customerId);

    long countByCustomerId(Long customerId);
}
