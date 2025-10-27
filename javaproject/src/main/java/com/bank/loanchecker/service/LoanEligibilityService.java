package com.bank.loanchecker.service;

import com.bank.loanchecker.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LoanEligibilityService {

    public LoanEligibilityResult checkEligibility(LoanApplication application) {
        Customer customer = application.getCustomer();
        LoanType loanType = application.getLoanType();
        Double requestedAmount = application.getRequestedAmount();
        Integer tenureYears = application.getTenureYears();

        // Basic eligibility checks
        if (!isBasicEligibilityMet(customer, loanType, requestedAmount, tenureYears)) {
            return createRejectedResult("Basic eligibility criteria not met", customer, loanType);
        }

        // Calculate maximum eligible amount
        Double maxEligibleAmount = calculateMaxEligibleAmount(customer, loanType);
        
        if (maxEligibleAmount < loanType.getMinAmount()) {
            return createRejectedResult("Income too low for this loan type", customer, loanType);
        }

        // Determine approved amount
        Double approvedAmount = Math.min(requestedAmount, maxEligibleAmount);
        approvedAmount = Math.max(approvedAmount, loanType.getMinAmount());
        approvedAmount = Math.min(approvedAmount, loanType.getMaxAmount());

        // Calculate EMI
        Double monthlyEMI = calculateEMI(approvedAmount, loanType.getInterestRate(), tenureYears);

        // Final affordability check
        if (!isAffordable(customer, monthlyEMI)) {
            return createRejectedResult("EMI exceeds affordable limit based on income", customer, loanType);
        }

        return createApprovedResult(customer, loanType, approvedAmount, monthlyEMI, tenureYears);
    }

    private boolean isBasicEligibilityMet(Customer customer, LoanType loanType, Double requestedAmount, Integer tenureYears) {
        // Age check
        if (customer.getAge() < 21 || customer.getAge() > 60) {
            return false;
        }

        // Credit score check
        if (customer.getCreditScore() < 600) {
            return false;
        }

        // Amount range check
        if (requestedAmount < loanType.getMinAmount() || requestedAmount > loanType.getMaxAmount()) {
            return false;
        }

        // Tenure check
        if (tenureYears > loanType.getMaxTenureYears()) {
            return false;
        }

        // Employment type specific checks
        if (customer.getEmploymentType() == EmploymentType.RETIRED && 
            (loanType == LoanType.PERSONAL_LOAN || loanType == LoanType.CAR_LOAN)) {
            return false;
        }

        return true;
    }

    private Double calculateMaxEligibleAmount(Customer customer, LoanType loanType) {
        Double monthlyIncome = customer.getMonthlyIncome();
        Double existingEMI = customer.getExistingEMI();
        Integer creditScore = customer.getCreditScore();
        EmploymentType employmentType = customer.getEmploymentType();

        // Base multiplier based on loan type
        double baseMultiplier = getBaseMultiplier(loanType);
        
        // Employment type adjustment
        double employmentMultiplier = getEmploymentMultiplier(employmentType);
        
        // Credit score adjustment
        double creditMultiplier = getCreditScoreMultiplier(creditScore);
        
        // Calculate available income (after existing EMI)
        Double availableIncome = monthlyIncome - existingEMI;
        
        // Calculate maximum EMI (40% of available income)
        Double maxEMI = availableIncome * 0.4;
        
        // Calculate maximum loan amount based on EMI
        Double maxLoanAmount = calculateLoanAmountFromEMI(maxEMI, loanType.getInterestRate(), 
                                                         loanType.getMaxTenureYears());
        
        // Apply multipliers
        maxLoanAmount = maxLoanAmount * baseMultiplier * employmentMultiplier * creditMultiplier;
        
        return (double) Math.round(maxLoanAmount);
    }

    private double getBaseMultiplier(LoanType loanType) {
        switch (loanType) {
            case HOME_LOAN:
                return 1.0; // No adjustment
            case CAR_LOAN:
                return 0.8; // 80% of calculated amount
            case EDUCATION_LOAN:
                return 0.9; // 90% of calculated amount
            case PERSONAL_LOAN:
                return 0.6; // 60% of calculated amount
            default:
                return 1.0;
        }
    }

    private double getEmploymentMultiplier(EmploymentType employmentType) {
        switch (employmentType) {
            case SALARIED:
                return 1.0;
            case SELF_EMPLOYED:
                return 0.8;
            case BUSINESS_OWNER:
                return 0.7;
            case RETIRED:
                return 0.5;
            default:
                return 1.0;
        }
    }

    private double getCreditScoreMultiplier(Integer creditScore) {
        if (creditScore >= 750) {
            return 1.0;
        } else if (creditScore >= 700) {
            return 0.9;
        } else if (creditScore >= 650) {
            return 0.8;
        } else if (creditScore >= 600) {
            return 0.7;
        } else {
            return 0.5;
        }
    }

    private Double calculateLoanAmountFromEMI(Double monthlyEMI, Double interestRate, Integer tenureYears) {
        if (interestRate == 0) {
            return monthlyEMI * tenureYears * 12;
        }
        
        double monthlyRate = interestRate / (12 * 100);
        int totalMonths = tenureYears * 12;
        
        double loanAmount = monthlyEMI * ((Math.pow(1 + monthlyRate, totalMonths) - 1) / 
                                         (monthlyRate * Math.pow(1 + monthlyRate, totalMonths)));
        
        return loanAmount;
    }

    private Double calculateEMI(Double principal, Double interestRate, Integer tenureYears) {
        if (interestRate == 0) {
            return principal / (tenureYears * 12);
        }
        
        double monthlyRate = interestRate / (12 * 100);
        int totalMonths = tenureYears * 12;
        
        double emi = principal * monthlyRate * Math.pow(1 + monthlyRate, totalMonths) / 
                    (Math.pow(1 + monthlyRate, totalMonths) - 1);
        
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private boolean isAffordable(Customer customer, Double monthlyEMI) {
        Double availableIncome = customer.getMonthlyIncome() - customer.getExistingEMI();
        return monthlyEMI <= (availableIncome * 0.4);
    }

    private LoanEligibilityResult createApprovedResult(Customer customer, LoanType loanType, 
                                                      Double approvedAmount, Double monthlyEMI, 
                                                      Integer tenureYears) {
        String reason = generateApprovalReason(customer, loanType, approvedAmount);
        String recommendation = generateRecommendation(customer, loanType);
        
        return new LoanEligibilityResult(
            true,
            "APPROVED",
            reason,
            approvedAmount,
            monthlyEMI,
            loanType.getInterestRate(),
            tenureYears,
            recommendation
        );
    }

    private LoanEligibilityResult createRejectedResult(String reason, Customer customer, LoanType loanType) {
        String recommendation = generateRejectionRecommendation(customer, loanType, reason);
        
        return new LoanEligibilityResult(
            false,
            "REJECTED",
            reason,
            0.0,
            0.0,
            loanType.getInterestRate(),
            0,
            recommendation
        );
    }

    private String generateApprovalReason(Customer customer, LoanType loanType, Double approvedAmount) {
        StringBuilder reason = new StringBuilder();
        reason.append("Congratulations! Your loan application has been approved. ");
        reason.append("Based on your income of ₹").append(String.format("%.0f", customer.getMonthlyIncome()));
        reason.append(", credit score of ").append(customer.getCreditScore());
        reason.append(", and employment type (").append(customer.getEmploymentType().getDisplayName());
        reason.append("), you are eligible for a ").append(loanType.getDisplayName());
        reason.append(" of ₹").append(String.format("%.0f", approvedAmount));
        reason.append(".");
        return reason.toString();
    }

    private String generateRecommendation(Customer customer, LoanType loanType) {
        StringBuilder recommendation = new StringBuilder();
        recommendation.append("Recommendations: ");
        
        if (customer.getCreditScore() < 750) {
            recommendation.append("Consider improving your credit score for better interest rates. ");
        }
        
        if (customer.getExistingEMI() > customer.getMonthlyIncome() * 0.3) {
            recommendation.append("Your existing EMI is high; consider reducing other debts. ");
        }
        
        recommendation.append("Ensure timely EMI payments to maintain good credit standing.");
        
        return recommendation.toString();
    }

    private String generateRejectionRecommendation(Customer customer, LoanType loanType, String reason) {
        StringBuilder recommendation = new StringBuilder();
        recommendation.append("Recommendations to improve eligibility: ");
        
        if (customer.getCreditScore() < 600) {
            recommendation.append("Improve your credit score by paying bills on time and reducing debt. ");
        }
        
        if (customer.getMonthlyIncome() < 25000) {
            recommendation.append("Consider increasing your income or applying for a smaller loan amount. ");
        }
        
        if (customer.getAge() < 21) {
            recommendation.append("Wait until you reach the minimum age requirement. ");
        }
        
        if (customer.getAge() > 60) {
            recommendation.append("Consider applying with a co-applicant or guarantor. ");
        }
        
        recommendation.append("You can reapply after addressing these concerns.");
        
        return recommendation.toString();
    }
}
