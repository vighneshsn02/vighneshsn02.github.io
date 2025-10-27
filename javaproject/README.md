# Bank Loan Eligibility Checker

A comprehensive web application built with Spring Boot that allows customers to check their eligibility for different types of loans (Home Loan, Car Loan, Education Loan, Personal Loan). The system evaluates eligibility based on predefined business rules including income, age, credit score, existing loans, and employment type.

## Features

### 🏦 Loan Types Supported
- **Home Loan**: ₹5,00,000 - ₹5,00,00,000 (8.5% interest, up to 30 years)
- **Car Loan**: ₹1,00,000 - ₹20,00,000 (9.5% interest, up to 7 years)
- **Education Loan**: ₹50,000 - ₹10,00,000 (7.5% interest, up to 15 years)
- **Personal Loan**: ₹25,000 - ₹5,00,000 (12.0% interest, up to 5 years)

### 📊 Eligibility Criteria
- **Age**: 21-60 years (varies by loan type)
- **Credit Score**: Minimum 600
- **Income**: Minimum ₹10,000 monthly income
- **Employment Type**: Salaried, Self-Employed, Business Owner, Retired
- **EMI Affordability**: Maximum 40% of available income

### 🧮 Features
- **Real-time Eligibility Check**: Instant approval/rejection with detailed reasons
- **EMI Calculator**: Calculate monthly EMI for any loan amount
- **Comprehensive Business Rules**: Multi-factor eligibility assessment
- **Modern Web Interface**: Responsive design with Bootstrap
- **REST API**: Full API support for integration
- **Detailed Results**: Complete loan details including total interest and amount

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Spring Boot Test

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Web browser (Chrome, Firefox, Safari, Edge)

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd javaproject
```

### 2. Build the Application
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

### 4. Access the Application
Open your web browser and navigate to:
```
http://localhost:8080
```

## Usage

### Web Interface
1. **Fill Personal Information**: Name, age, email, phone number
2. **Enter Financial Details**: Monthly income, credit score, existing EMI, employment type
3. **Select Loan Details**: Loan type, requested amount, tenure
4. **Use EMI Calculator**: Calculate EMI for different scenarios
5. **Submit Application**: Get instant eligibility result

### REST API Endpoints

#### Check Eligibility
```http
POST /api/check-eligibility
Content-Type: application/json

{
  "customer": {
    "name": "John Doe",
    "age": 30,
    "email": "john@email.com",
    "phoneNumber": "9876543210",
    "monthlyIncome": 50000,
    "creditScore": 750,
    "existingEMI": 5000,
    "employmentType": "SALARIED"
  },
  "loanType": "HOME_LOAN",
  "requestedAmount": 2000000,
  "tenureYears": 20
}
```

#### Calculate EMI
```http
POST /api/calculate-emi?principal=1000000&interestRate=8.5&tenureYears=20
```

#### Get Loan Types
```http
GET /api/loan-types
```

#### Get Employment Types
```http
GET /api/employment-types
```

## Business Rules

### Eligibility Multipliers
- **Employment Type**:
  - Salaried: 100%
  - Self-Employed: 80%
  - Business Owner: 70%
  - Retired: 50%

- **Credit Score**:
  - 750+: 100%
  - 700-749: 90%
  - 650-699: 80%
  - 600-649: 70%
  - Below 600: 50%

- **Loan Type**:
  - Home Loan: 100%
  - Car Loan: 80%
  - Education Loan: 90%
  - Personal Loan: 60%

### Age Restrictions
- **Home Loan**: 21-60 years
- **Car Loan**: 21-60 years
- **Education Loan**: 18-60 years
- **Personal Loan**: 21-60 years
- **Retired**: Only Home Loan and Education Loan allowed

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Classes
```bash
mvn test -Dtest=LoanEligibilityServiceTest
mvn test -Dtest=LoanControllerTest
```

### Test Coverage
The application includes comprehensive test coverage:
- Unit tests for business logic
- Integration tests for REST APIs
- Controller tests for web endpoints
- Validation tests for form inputs

## Project Structure

```
src/
├── main/
│   ├── java/com/bank/loanchecker/
│   │   ├── LoanEligibilityCheckerApplication.java
│   │   ├── controller/
│   │   │   └── LoanController.java
│   │   ├── model/
│   │   │   ├── Customer.java
│   │   │   ├── EmploymentType.java
│   │   │   ├── LoanApplication.java
│   │   │   ├── LoanEligibilityResult.java
│   │   │   └── LoanType.java
│   │   └── service/
│   │       └── LoanEligibilityService.java
│   └── resources/
│       └── templates/
│           ├── index.html
│           └── result.html
└── test/
    └── java/com/bank/loanchecker/
        ├── LoanEligibilityCheckerApplicationTest.java
        ├── controller/
        │   └── LoanControllerTest.java
        └── service/
            └── LoanEligibilityServiceTest.java
```

## API Response Examples

### Approved Loan Response
```json
{
  "eligible": true,
  "decision": "APPROVED",
  "reason": "Congratulations! Your loan application has been approved...",
  "approvedAmount": 2000000.0,
  "monthlyEMI": 15000.0,
  "interestRate": 8.5,
  "tenureYears": 20,
  "recommendation": "Ensure timely EMI payments..."
}
```

### Rejected Loan Response
```json
{
  "eligible": false,
  "decision": "REJECTED",
  "reason": "Credit score too low for loan approval",
  "approvedAmount": 0.0,
  "monthlyEMI": 0.0,
  "interestRate": 8.5,
  "tenureYears": 0,
  "recommendation": "Improve your credit score..."
}
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.

---

**Note**: This is a demonstration application. In a production environment, additional security measures, database persistence, and more sophisticated business rules would be implemented.


