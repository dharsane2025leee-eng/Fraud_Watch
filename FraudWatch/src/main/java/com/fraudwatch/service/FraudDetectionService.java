package com.fraudwatch.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fraudwatch.entity.FraudRule;
import com.fraudwatch.entity.Transaction;

@Service
public class FraudDetectionService {

    private final FraudRuleService fraudRuleService;
    private final TransactionService transactionService;

    public FraudDetectionService(
            FraudRuleService fraudRuleService,
            TransactionService transactionService) {

        this.fraudRuleService = fraudRuleService;
        this.transactionService = transactionService;
    }

    public Transaction checkTransaction(
            Transaction transaction) {

        // Set transaction time before fraud checking
        if (transaction.getTransactionTime() == null) {
            transaction.setTransactionTime(
                    LocalDateTime.now());
        }

        List<FraudRule> rules =
                fraudRuleService.getEnabledRules();

        StringBuilder reasons = new StringBuilder();

        for (FraudRule rule : rules) {

            // Rule 1: Amount Threshold
            if ("AMOUNT_THRESHOLD"
                    .equalsIgnoreCase(rule.getRuleType())) {

                if (transaction.getAmount() != null
                        && rule.getThreshold() != null
                        && transaction.getAmount()
                        > rule.getThreshold()) {

                    reasons.append(
                            "Amount exceeds configured threshold; ");
                }
            }

            // Rule 2: Rapid Repeat Transaction
            if ("RAPID_REPEAT"
                    .equalsIgnoreCase(rule.getRuleType())) {

                if (transaction.getAccountNumber() != null
                        && transaction.getTransactionTime() != null
                        && rule.getThreshold() != null) {

                    LocalDateTime startTime =
                            transaction.getTransactionTime()
                                    .minusMinutes(
                                            rule.getThreshold()
                                                    .longValue());

                    List<Transaction> recentTransactions =
                            transactionService
                                    .getTransactionsByAccount(
                                            transaction
                                                    .getAccountNumber());

                    for (Transaction oldTransaction
                            : recentTransactions) {

                        if (oldTransaction.getId() != null
                                && oldTransaction
                                        .getTransactionTime() != null
                                && oldTransaction
                                        .getTransactionTime()
                                        .isAfter(startTime)
                                && oldTransaction
                                        .getTransactionTime()
                                        .isBefore(
                                                transaction
                                                        .getTransactionTime())) {

                            reasons.append(
                                    "Rapid repeat transaction detected; ");

                            break;
                        }
                    }
                }
            }
        }

        // Set fraud status
        if (reasons.length() > 0) {

            transaction.setStatus("FLAGGED");

            transaction.setFlagReason(
                    reasons.toString());

        } else {

            transaction.setStatus("NORMAL");

            transaction.setFlagReason("");
        }

        return transaction;
    }
}