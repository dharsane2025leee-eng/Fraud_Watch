package com.fraudwatch.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fraudwatch.entity.FraudRule;
import com.fraudwatch.service.FraudRuleService;

@RestController
@RequestMapping("/api/rules")
public class FraudRuleController {

    private final FraudRuleService fraudRuleService;

    public FraudRuleController(
            FraudRuleService fraudRuleService) {

        this.fraudRuleService = fraudRuleService;
    }

    @PostMapping
    public ResponseEntity<FraudRule> createRule(
            @jakarta.validation.Valid
            @RequestBody FraudRule rule) {

        return ResponseEntity.ok(
                fraudRuleService.createRule(rule));
    }

    @GetMapping
    public ResponseEntity<List<FraudRule>> getAllRules() {

        return ResponseEntity.ok(
                fraudRuleService.getAllRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FraudRule> getRuleById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                fraudRuleService.getRuleById(id));
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<FraudRule>> getEnabledRules() {

        return ResponseEntity.ok(
                fraudRuleService.getEnabledRules());
    }

    @GetMapping("/type/{ruleType}")
    public ResponseEntity<List<FraudRule>> getRulesByType(
            @PathVariable String ruleType) {

        return ResponseEntity.ok(
                fraudRuleService.getRulesByType(ruleType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FraudRule> updateRule(
            @PathVariable Long id,
            @jakarta.validation.Valid
            @RequestBody FraudRule rule) {

        return ResponseEntity.ok(
                fraudRuleService.updateRule(id, rule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRule(
            @PathVariable Long id) {

        fraudRuleService.deleteRule(id);

        return ResponseEntity.ok(
                "Fraud rule deleted successfully");
    }
}