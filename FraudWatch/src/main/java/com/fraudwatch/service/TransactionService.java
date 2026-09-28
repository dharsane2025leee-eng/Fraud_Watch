package com.fraudwatch.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fraudwatch.entity.Transaction;
import com.fraudwatch.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(
            TransactionRepository transactionRepository) {

        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(
            Transaction transaction) {

        if (transaction.getTransactionTime() == null) {
            transaction.setTransactionTime(
                    LocalDateTime.now());
        }

        if (transaction.getStatus() == null) {
            transaction.setStatus("NORMAL");
        }

        if (transaction.getFlagReason() == null) {
            transaction.setFlagReason("");
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {

        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {

        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Transaction not found"));
    }

    public List<Transaction> getTransactionsByAccount(
            String accountNumber) {

        return transactionRepository
                .findByAccountNumber(accountNumber);
    }

    public List<Transaction> getFlaggedTransactions() {

        return transactionRepository
                .findByStatus("FLAGGED");
    }

    public Transaction updateTransactionStatus(
            Long id,
            String status) {

        Transaction transaction =
                getTransactionById(id);

        transaction.setStatus(status);

        return transactionRepository.save(transaction);
    }
}