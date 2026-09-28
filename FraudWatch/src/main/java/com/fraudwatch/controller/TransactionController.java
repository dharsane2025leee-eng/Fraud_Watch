package com.fraudwatch.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fraudwatch.entity.Transaction;
import com.fraudwatch.service.FraudDetectionService;
import com.fraudwatch.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final FraudDetectionService fraudDetectionService;

    public TransactionController(
            TransactionService transactionService,
            FraudDetectionService fraudDetectionService) {

        this.transactionService = transactionService;
        this.fraudDetectionService = fraudDetectionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody Transaction transaction) {

        Transaction checkedTransaction =
                fraudDetectionService
                        .checkTransaction(transaction);

        return ResponseEntity.ok(
                transactionService
                        .createTransaction(checkedTransaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>>
            getAllTransactions() {

        return ResponseEntity.ok(
                transactionService
                        .getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction>
            getTransactionById(
                    @PathVariable Long id) {

        return ResponseEntity.ok(
                transactionService
                        .getTransactionById(id));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>>
            getTransactionsByAccount(
                    @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                transactionService
                        .getTransactionsByAccount(
                                accountNumber));
    }

    @GetMapping("/flagged")
    public ResponseEntity<List<Transaction>>
            getFlaggedTransactions() {

        return ResponseEntity.ok(
                transactionService
                        .getFlaggedTransactions());
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Transaction>
            updateTransactionStatus(
                    @PathVariable Long id,
                    @PathVariable String status) {

        return ResponseEntity.ok(
                transactionService
                        .updateTransactionStatus(
                                id,
                                status));
    }
}