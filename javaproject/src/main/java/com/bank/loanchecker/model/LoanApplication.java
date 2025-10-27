package com.bank.loanchecker.model;

import jakarta.validation.constraints.*;

public class LoanApplication {
    @NotNull(message = "Customer information is required")
    private Customer customer;

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Loan amount is required")
    @Min(value = 1, message = "Loan amount must be greater than 0")
    private Double requestedAmount;

    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 year")
    @Max(value = 30, message = "Tenure cannot exceed 30 years")
    private Integer tenureYears;

    // Constructors
    public LoanApplication() {}

    public LoanApplication(Customer customer, LoanType loanType, Double requestedAmount, Integer tenureYears) {
        this.customer = customer;
        this.loanType = loanType;
        this.requestedAmount = requestedAmount;
        this.tenureYears = tenureYears;
    }

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getTenureYears() {
        return tenureYears;
    }

    public void setTenureYears(Integer tenureYears) {
        this.tenureYears = tenureYears;
    }
}


