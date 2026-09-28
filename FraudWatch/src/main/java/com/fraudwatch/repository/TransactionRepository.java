package com.fraudwatch.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fraudwatch.entity.Transaction;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountNumber(
            String accountNumber);

    List<Transaction> findByStatus(
            String status);

    List<Transaction> findByAccountNumberAndTransactionTimeAfter(
            String accountNumber,
            LocalDateTime time);
}