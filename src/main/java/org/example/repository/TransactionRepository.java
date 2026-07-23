package org.example.repository;

import org.example.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions for a specific account.
     * Results are ordered by timestamp (newest first).
     *
     * @param accountId the account ID
     * @return List of transactions for the account, ordered by timestamp descending
     */
    List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);

    /**
     * Counts the number of transactions for a specific account.
     *
     * @param accountId the account ID
     * @return the number of transactions
     */
    long countByAccountId(Long accountId);
}
