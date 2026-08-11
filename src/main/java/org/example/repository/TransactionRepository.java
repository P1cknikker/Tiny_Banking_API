package org.example.repository;

import org.example.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query("SELECT t FROM Transaction t JOIN FETCH t.account WHERE t.account.id = :accountId ORDER BY t.timestamp DESC")
    /**
     * Finds all transactions for a specific account.
     * Results are ordered by timestamp (newest first).
     *
     * @param accountId the account ID
     * @return List of transactions for the account, ordered by timestamp descending
     */
    List<Transaction> findByAccountIdOrderByTimestampDesc(@Param("accountId") Long accountId);

    /**
     * Counts the number of transactions for a specific account.
     *
     * @param accountId the account ID
     * @return the number of transactions
     */
    long countByAccountId(Long accountId);
}
