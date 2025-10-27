package com.bank.loanchecker.controller;

import com.bank.loanchecker.model.*;
import com.bank.loanchecker.service.LoanEligibilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanEligibilityService loanEligibilityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("loanApplication"))
                .andExpect(model().attributeExists("loanTypes"))
                .andExpect(model().attributeExists("employmentTypes"));
    }

    @Test
    void testGetLoanTypesApi() throws Exception {
        mockMvc.perform(get("/api/loan-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].displayName").exists());
    }

    @Test
    void testGetEmploymentTypesApi() throws Exception {
        mockMvc.perform(get("/api/employment-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].displayName").exists());
    }

    @Test
    void testCheckEligibilityApi_Success() throws Exception {
        // Create test data
        Customer customer = createValidCustomer();
        LoanApplication application = createValidLoanApplication(customer);
        LoanEligibilityResult result = createApprovedResult();

        when(loanEligibilityService.checkEligibility(any(LoanApplication.class)))
                .thenReturn(result);

        mockMvc.perform(post("/api/check-eligibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.decision").value("APPROVED"))
                .andExpect(jsonPath("$.approvedAmount").exists())
                .andExpect(jsonPath("$.monthlyEMI").exists());
    }

    @Test
    void testCheckEligibilityApi_Rejected() throws Exception {
        // Create test data
        Customer customer = createValidCustomer();
        customer.setCreditScore(500); // Low credit score
        LoanApplication application = createValidLoanApplication(customer);
        LoanEligibilityResult result = createRejectedResult();

        when(loanEligibilityService.checkEligibility(any(LoanApplication.class)))
                .thenReturn(result);

        mockMvc.perform(post("/api/check-eligibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eligible").value(false))
                .andExpect(jsonPath("$.decision").value("REJECTED"))
                .andExpect(jsonPath("$.reason").exists());
    }

    @Test
    void testCalculateEMIApi() throws Exception {
        mockMvc.perform(post("/api/calculate-emi")
                .param("principal", "1000000")
                .param("interestRate", "8.5")
                .param("tenureYears", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void testCalculateEMIApi_ZeroInterest() throws Exception {
        mockMvc.perform(post("/api/calculate-emi")
                .param("principal", "120000")
                .param("interestRate", "0")
                .param("tenureYears", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(2000.0)); // 120000 / (5 * 12) = 2000
    }

    @Test
    void testCheckEligibilityForm_Success() throws Exception {
        LoanEligibilityResult result = createApprovedResult();
        when(loanEligibilityService.checkEligibility(any(LoanApplication.class)))
                .thenReturn(result);

        mockMvc.perform(post("/check-eligibility")
                .param("customer.name", "John Doe")
                .param("customer.age", "30")
                .param("customer.email", "john@email.com")
                .param("customer.phoneNumber", "9876543210")
                .param("customer.monthlyIncome", "50000")
                .param("customer.creditScore", "750")
                .param("customer.existingEMI", "5000")
                .param("customer.employmentType", "SALARIED")
                .param("loanType", "HOME_LOAN")
                .param("requestedAmount", "2000000")
                .param("tenureYears", "20"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("loanApplication"));
    }

    @Test
    void testCheckEligibilityForm_ValidationError() throws Exception {
        mockMvc.perform(post("/check-eligibility")
                .param("customer.name", "") // Empty name
                .param("customer.age", "17") // Below minimum age
                .param("customer.email", "invalid-email") // Invalid email
                .param("customer.phoneNumber", "123") // Invalid phone
                .param("customer.monthlyIncome", "5000") // Below minimum income
                .param("customer.creditScore", "200") // Below minimum credit score
                .param("customer.existingEMI", "-1000") // Negative EMI
                .param("customer.employmentType", "SALARIED")
                .param("loanType", "HOME_LOAN")
                .param("requestedAmount", "100000") // Below minimum amount
                .param("tenureYears", "35")) // Above maximum tenure
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors());
    }

    private Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setAge(30);
        customer.setEmail("john.doe@email.com");
        customer.setPhoneNumber("9876543210");
        customer.setMonthlyIncome(50000.0);
        customer.setCreditScore(750);
        customer.setExistingEMI(5000.0);
        customer.setEmploymentType(EmploymentType.SALARIED);
        return customer;
    }

    private LoanApplication createValidLoanApplication(Customer customer) {
        LoanApplication application = new LoanApplication();
        application.setCustomer(customer);
        application.setLoanType(LoanType.HOME_LOAN);
        application.setRequestedAmount(2000000.0);
        application.setTenureYears(20);
        return application;
    }

    private LoanEligibilityResult createApprovedResult() {
        return new LoanEligibilityResult(
                true,
                "APPROVED",
                "Congratulations! Your loan application has been approved.",
                2000000.0,
                15000.0,
                8.5,
                20,
                "Ensure timely EMI payments to maintain good credit standing."
        );
    }

    private LoanEligibilityResult createRejectedResult() {
        return new LoanEligibilityResult(
                false,
                "REJECTED",
                "Credit score too low for loan approval.",
                0.0,
                0.0,
                8.5,
                0,
                "Improve your credit score by paying bills on time."
        );
    }
}


