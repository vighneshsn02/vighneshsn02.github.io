package com.bank.loanchecker.model;

import jakarta.validation.constraints.*;

public class Customer {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 65, message = "Age must not exceed 65")
    private Integer age;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotNull(message = "Monthly income is required")
    @Min(value = 10000, message = "Monthly income must be at least ₹10,000")
    private Double monthlyIncome;

    @NotNull(message = "Credit score is required")
    @Min(value = 300, message = "Credit score must be at least 300")
    @Max(value = 900, message = "Credit score must not exceed 900")
    private Integer creditScore;

    @NotNull(message = "Existing EMI is required")
    @Min(value = 0, message = "Existing EMI cannot be negative")
    private Double existingEMI;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    // Constructors
    public Customer() {}

    public Customer(String name, Integer age, String email, String phoneNumber, 
                   Double monthlyIncome, Integer creditScore, Double existingEMI, 
                   EmploymentType employmentType) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.monthlyIncome = monthlyIncome;
        this.creditScore = creditScore;
        this.existingEMI = existingEMI;
        this.employmentType = employmentType;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public Double getExistingEMI() {
        return existingEMI;
    }

    public void setExistingEMI(Double existingEMI) {
        this.existingEMI = existingEMI;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
}


