package com.bank.loanchecker.controller;

import com.bank.loanchecker.model.*;
import com.bank.loanchecker.service.LoanEligibilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class LoanController {

    @Autowired
    private LoanEligibilityService loanEligibilityService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("loanApplication", new LoanApplication());
        model.addAttribute("loanTypes", Arrays.asList(LoanType.values()));
        model.addAttribute("employmentTypes", Arrays.asList(EmploymentType.values()));
        return "index";
    }

    @PostMapping("/check-eligibility")
    public String checkEligibility(@Valid @ModelAttribute LoanApplication loanApplication, 
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loanTypes", Arrays.asList(LoanType.values()));
            model.addAttribute("employmentTypes", Arrays.asList(EmploymentType.values()));
            return "index";
        }

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(loanApplication);
        model.addAttribute("result", result);
        model.addAttribute("loanApplication", loanApplication);
        
        return "result";
    }

    @GetMapping("/api/loan-types")
    @ResponseBody
    public ResponseEntity<List<LoanType>> getLoanTypes() {
        return ResponseEntity.ok(Arrays.asList(LoanType.values()));
    }

    @GetMapping("/api/employment-types")
    @ResponseBody
    public ResponseEntity<List<EmploymentType>> getEmploymentTypes() {
        return ResponseEntity.ok(Arrays.asList(EmploymentType.values()));
    }

    @PostMapping("/api/check-eligibility")
    @ResponseBody
    public ResponseEntity<LoanEligibilityResult> checkEligibilityApi(@Valid @RequestBody LoanApplication loanApplication) {
        LoanEligibilityResult result = loanEligibilityService.checkEligibility(loanApplication);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/calculate-emi")
    @ResponseBody
    public ResponseEntity<Double> calculateEMI(@RequestParam Double principal, 
                                             @RequestParam Double interestRate, 
                                             @RequestParam Integer tenureYears) {
        if (interestRate == 0) {
            return ResponseEntity.ok(principal / (tenureYears * 12));
        }
        
        double monthlyRate = interestRate / (12 * 100);
        int totalMonths = tenureYears * 12;
        
        double emi = principal * monthlyRate * Math.pow(1 + monthlyRate, totalMonths) / 
                    (Math.pow(1 + monthlyRate, totalMonths) - 1);
        
        return ResponseEntity.ok(Math.round(emi * 100.0) / 100.0);
    }
}


