package com.bank.loanchecker.model;

public class LoanEligibilityResult {
    private boolean eligible;
    private String decision;
    private String reason;
    private Double approvedAmount;
    private Double monthlyEMI;
    private Double interestRate;
    private Integer tenureYears;
    private String recommendation;

    // Constructors
    public LoanEligibilityResult() {}

    public LoanEligibilityResult(boolean eligible, String decision, String reason, 
                                Double approvedAmount, Double monthlyEMI, 
                                Double interestRate, Integer tenureYears, String recommendation) {
        this.eligible = eligible;
        this.decision = decision;
        this.reason = reason;
        this.approvedAmount = approvedAmount;
        this.monthlyEMI = monthlyEMI;
        this.interestRate = interestRate;
        this.tenureYears = tenureYears;
        this.recommendation = recommendation;
    }

    // Getters and Setters
    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Double getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(Double approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public Double getMonthlyEMI() {
        return monthlyEMI;
    }

    public void setMonthlyEMI(Double monthlyEMI) {
        this.monthlyEMI = monthlyEMI;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureYears() {
        return tenureYears;
    }

    public void setTenureYears(Integer tenureYears) {
        this.tenureYears = tenureYears;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}


