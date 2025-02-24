package com.wiftwift.controller;

import com.wiftwift.dto.DebtConfirmRequest;
import com.wiftwift.dto.DebtPaidRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debts")
public class DebtController {

//    @Autowired
//    private DebtService debtService;
//
//    @PostMapping("/mark-paid")
//    public String markDebtPaid(@RequestBody DebtPaidRequest request) {
//        debtService.markDebtPaid(request.getFromUser(), request.getToUser(), request.getAmount());
//        return "Долг отмечен как выполненный";
//    }
//
//    @PostMapping("/confirm")
//    public String confirmDebtPayment(@RequestBody DebtConfirmRequest request) {
//        debtService.confirmDebtPayment(request.getFromUser(), request.getToUser(), request.getAmount());
//        return "Исполнение долга подтверждено";
//    }


}

