package com.bank.loanchecker.model;

public enum EmploymentType {
    SALARIED("Salaried"),
    SELF_EMPLOYED("Self Employed"),
    BUSINESS_OWNER("Business Owner"),
    RETIRED("Retired");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


