package com.bank.loanchecker.model;

public enum LoanType {
    HOME_LOAN("Home Loan", 8.5, 30, 500000, 50000000),
    CAR_LOAN("Car Loan", 9.5, 7, 100000, 2000000),
    EDUCATION_LOAN("Education Loan", 7.5, 15, 50000, 1000000),
    PERSONAL_LOAN("Personal Loan", 12.0, 5, 25000, 500000);

    private final String displayName;
    private final double interestRate;
    private final int maxTenureYears;
    private final double minAmount;
    private final double maxAmount;

    LoanType(String displayName, double interestRate, int maxTenureYears, double minAmount, double maxAmount) {
        this.displayName = displayName;
        this.interestRate = interestRate;
        this.maxTenureYears = maxTenureYears;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getMaxTenureYears() {
        return maxTenureYears;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }
}


