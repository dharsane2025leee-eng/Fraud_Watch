package com.fraudwatch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fraudwatch.entity.FraudRule;
import com.fraudwatch.repository.FraudRuleRepository;

@Service
public class FraudRuleService {

    private final FraudRuleRepository fraudRuleRepository;

    public FraudRuleService(
            FraudRuleRepository fraudRuleRepository) {

        this.fraudRuleRepository = fraudRuleRepository;
    }

    public FraudRule createRule(FraudRule rule) {

        if (rule.getEnabled() == null) {
            rule.setEnabled(true);
        }

        return fraudRuleRepository.save(rule);
    }

    public List<FraudRule> getAllRules() {

        return fraudRuleRepository.findAll();
    }

    public FraudRule getRuleById(Long id) {

        return fraudRuleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Fraud rule not found"));
    }

    public List<FraudRule> getEnabledRules() {

        return fraudRuleRepository
                .findByEnabledTrue();
    }

    public List<FraudRule> getRulesByType(
            String ruleType) {

        return fraudRuleRepository
                .findByRuleType(ruleType);
    }

    public FraudRule updateRule(
            Long id,
            FraudRule updatedRule) {

        FraudRule rule = getRuleById(id);

        rule.setRuleName(updatedRule.getRuleName());
        rule.setRuleType(updatedRule.getRuleType());
        rule.setThreshold(updatedRule.getThreshold());
        rule.setEnabled(updatedRule.getEnabled());
        rule.setDescription(updatedRule.getDescription());

        return fraudRuleRepository.save(rule);
    }

    public void deleteRule(Long id) {

        FraudRule rule = getRuleById(id);

        fraudRuleRepository.delete(rule);
    }
}