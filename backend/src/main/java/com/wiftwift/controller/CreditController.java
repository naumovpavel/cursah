package com.wiftwift.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.wiftwift.service.CreditService;
import com.wiftwift.dto.ReturnCreditRequest;
import com.wiftwift.model.CreditNode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CreditController {
    private final CreditService creditService;

    @GetMapping("/credit/to/{id}")
    public List<CreditNode> getCreditsTo(@PathVariable Long id) {
        return creditService.getCreditsTo(id);
    }

    @GetMapping("/credit/from/{id}")
    public List<CreditNode> getCreditsFrom(@PathVariable Long id) {
        return creditService.getCreditsFrom(id);
    }

    @PostMapping("/credit/return")
    public ResponseEntity<?> returnCredit(@RequestBody ReturnCreditRequest request) {        
        try {
            CreditNode result = creditService.returnCredit(request.getCreditId(), request.getAmount());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }

    @PostMapping("/credit/approve/{id}")
    public void approveCredit(@PathVariable Long id) {
        creditService.approveCredit(id);
    }

    @PostMapping("/group/close/{id}")
    public void closeGroup(@PathVariable Long id) {
        creditService.closeGroup(id);
    }
}

