package com.bank.loanchecker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.banner-mode=off",
    "logging.level.org.springframework=WARN"
})
class LoanEligibilityCheckerApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures that the Spring Boot application context loads successfully
    }
}


