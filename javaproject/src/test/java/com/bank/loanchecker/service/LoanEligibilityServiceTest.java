package com.bank.loanchecker.service;

import com.bank.loanchecker.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanEligibilityServiceTest {

    @InjectMocks
    private LoanEligibilityService loanEligibilityService;

    private Customer validCustomer;
    private LoanApplication validApplication;

    @BeforeEach
    void setUp() {
        validCustomer = new Customer();
        validCustomer.setName("John Doe");
        validCustomer.setAge(30);
        validCustomer.setEmail("john.doe@email.com");
        validCustomer.setPhoneNumber("9876543210");
        validCustomer.setMonthlyIncome(50000.0);
        validCustomer.setCreditScore(750);
        validCustomer.setExistingEMI(5000.0);
        validCustomer.setEmploymentType(EmploymentType.SALARIED);

        validApplication = new LoanApplication();
        validApplication.setCustomer(validCustomer);
        validApplication.setLoanType(LoanType.HOME_LOAN);
        validApplication.setRequestedAmount(2000000.0);
        validApplication.setTenureYears(20);
    }

    @Test
    void testHomeLoanEligibility_Approved() {
        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertEquals("APPROVED", result.getDecision());
        assertNotNull(result.getApprovedAmount());
        assertNotNull(result.getMonthlyEMI());
        assertTrue(result.getApprovedAmount() > 0);
        assertTrue(result.getMonthlyEMI() > 0);
    }

    @Test
    void testCarLoanEligibility_Approved() {
        validApplication.setLoanType(LoanType.CAR_LOAN);
        validApplication.setRequestedAmount(500000.0);
        validApplication.setTenureYears(5);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertEquals("APPROVED", result.getDecision());
        assertEquals(LoanType.CAR_LOAN.getInterestRate(), result.getInterestRate());
    }

    @Test
    void testPersonalLoanEligibility_Approved() {
        validApplication.setLoanType(LoanType.PERSONAL_LOAN);
        validApplication.setRequestedAmount(100000.0);
        validApplication.setTenureYears(3);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertEquals("APPROVED", result.getDecision());
        assertEquals(LoanType.PERSONAL_LOAN.getInterestRate(), result.getInterestRate());
    }

    @Test
    void testEducationLoanEligibility_Approved() {
        validApplication.setLoanType(LoanType.EDUCATION_LOAN);
        validApplication.setRequestedAmount(300000.0);
        validApplication.setTenureYears(10);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertEquals("APPROVED", result.getDecision());
        assertEquals(LoanType.EDUCATION_LOAN.getInterestRate(), result.getInterestRate());
    }

    @Test
    void testLowCreditScore_Rejected() {
        validCustomer.setCreditScore(500);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
        assertTrue(result.getReason().contains("Basic eligibility criteria not met"));
    }

    @Test
    void testLowAge_Rejected() {
        validCustomer.setAge(18);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
    }

    @Test
    void testHighAge_Rejected() {
        validCustomer.setAge(65);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
    }

    @Test
    void testLowIncome_Rejected() {
        validCustomer.setMonthlyIncome(15000.0);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
        assertTrue(result.getReason().contains("Income too low"));
    }

    @Test
    void testHighExistingEMI_Rejected() {
        validCustomer.setExistingEMI(30000.0); // More than 40% of income

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
        assertTrue(result.getReason().contains("EMI exceeds affordable limit"));
    }

    @Test
    void testRetiredPersonalLoan_Rejected() {
        validCustomer.setEmploymentType(EmploymentType.RETIRED);
        validApplication.setLoanType(LoanType.PERSONAL_LOAN);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
    }

    @Test
    void testRetiredCarLoan_Rejected() {
        validCustomer.setEmploymentType(EmploymentType.RETIRED);
        validApplication.setLoanType(LoanType.CAR_LOAN);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
    }

    @Test
    void testSelfEmployedReducedAmount() {
        validCustomer.setEmploymentType(EmploymentType.SELF_EMPLOYED);
        validApplication.setLoanType(LoanType.HOME_LOAN);
        validApplication.setRequestedAmount(5000000.0);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertEquals("APPROVED", result.getDecision());
        // Self-employed should get reduced amount due to multiplier
        assertTrue(result.getApprovedAmount() < validApplication.getRequestedAmount());
    }

    @Test
    void testBusinessOwnerReducedAmount() {
        validCustomer.setEmploymentType(EmploymentType.BUSINESS_OWNER);
        validApplication.setLoanType(LoanType.HOME_LOAN);
        validApplication.setRequestedAmount(5000000.0);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertEquals("APPROVED", result.getDecision());
        // Business owner should get reduced amount due to multiplier
        assertTrue(result.getApprovedAmount() < validApplication.getRequestedAmount());
    }

    @Test
    void testHighCreditScoreBonus() {
        validCustomer.setCreditScore(800);

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertEquals("APPROVED", result.getDecision());
        assertNotNull(result.getRecommendation());
    }

    @Test
    void testEMICalculation() {
        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertNotNull(result.getMonthlyEMI());
        assertTrue(result.getMonthlyEMI() > 0);
        
        // Verify EMI calculation is reasonable
        double expectedEMI = calculateExpectedEMI(result.getApprovedAmount(), 
                                                 result.getInterestRate(), 
                                                 result.getTenureYears());
        assertEquals(expectedEMI, result.getMonthlyEMI(), 1.0); // Allow 1 rupee tolerance
    }

    @Test
    void testLoanAmountWithinLimits() {
        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertTrue(result.isEligible());
        assertTrue(result.getApprovedAmount() >= LoanType.HOME_LOAN.getMinAmount());
        assertTrue(result.getApprovedAmount() <= LoanType.HOME_LOAN.getMaxAmount());
    }

    @Test
    void testTenureWithinLimits() {
        validApplication.setTenureYears(35); // Exceeds max tenure

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
    }

    @Test
    void testAmountBelowMinimum() {
        validApplication.setRequestedAmount(100000.0); // Below home loan minimum

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
    }

    @Test
    void testAmountAboveMaximum() {
        validApplication.setRequestedAmount(60000000.0); // Above home loan maximum

        LoanEligibilityResult result = loanEligibilityService.checkEligibility(validApplication);

        assertFalse(result.isEligible());
        assertEquals("REJECTED", result.getDecision());
    }

    private double calculateExpectedEMI(double principal, double interestRate, int tenureYears) {
        if (interestRate == 0) {
            return principal / (tenureYears * 12);
        }
        
        double monthlyRate = interestRate / (12 * 100);
        int totalMonths = tenureYears * 12;
        
        return principal * monthlyRate * Math.pow(1 + monthlyRate, totalMonths) / 
               (Math.pow(1 + monthlyRate, totalMonths) - 1);
    }
}


