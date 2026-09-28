package com.fraudwatch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fraudwatch.entity.FraudRule;

public interface FraudRuleRepository
        extends JpaRepository<FraudRule, Long> {

    List<FraudRule> findByEnabledTrue();

    List<FraudRule> findByRuleType(String ruleType);
}