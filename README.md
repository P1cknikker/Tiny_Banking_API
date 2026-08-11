Tiny Banking API

Small Spring Boot backend for managing customers, accounts, and transactions.

Languages: English · Deutsch

Requirements





JDK 17 (or 21; avoid Java 25 for tests if possible)



Maven 3.8+



Optional: IntelliJ IDEA

Run

mvn spring-boot:run

Or start BankingApplication in IntelliJ.







Resource



URL





API



http://localhost:8080





H2 Console



http://localhost:8080/h2-console

H2 login





JDBC URL: jdbc:h2:mem:bankingdb



User: sa



Password: (empty)

Sample data from data.sql is loaded on startup (2 customers, 3 accounts, 4 transactions).

API Endpoints

Customers







Method



Path



Description





GET



/customers



List all customers





POST



/customers



Create customer





GET



/customers/{id}



Get customer by ID





PUT



/customers/{id}



Update customer





DELETE



/customers/{id}



Delete customer





GET



/customers/{id}/accounts



List accounts for a customer

Accounts & transactions







Method



Path



Description





GET



/accounts



List all accounts





POST



/accounts



Create account





GET



/accounts/{id}



Get account by ID





POST



/accounts/{id}/deposit



Deposit





POST



/accounts/{id}/withdraw



Withdraw





GET



/accounts/{id}/transactions



List transactions for an account

Example requests

# Customers
curl http://localhost:8080/customers

curl -X POST http://localhost:8080/customers \
-H "Content-Type: application/json" \
-d "{\"name\":\"Lisa\",\"email\":\"lisa@test.de\"}"

# Account
curl -X POST http://localhost:8080/accounts \
-H "Content-Type: application/json" \
-d "{\"iban\":\"DE89370400440532013000\",\"customerId\":1}"

# Deposit / withdraw
curl -X POST http://localhost:8080/accounts/1/deposit \
-H "Content-Type: application/json" \
-d "{\"amount\":100.50}"

curl -X POST http://localhost:8080/accounts/1/withdraw \
-H "Content-Type: application/json" \
-d "{\"amount\":20}"

# Balance & transactions
curl http://localhost:8080/accounts/1
curl http://localhost:8080/accounts/1/transactions

Business rules





A customer may have multiple accounts



Withdrawals only if the balance is sufficient



Amount must be ≥ 0.01



IBAN and email must be unique



Missing resources → 404



Customers with existing accounts cannot be deleted → 409

Project structure

src/main/java/org/example/
├── BankingApplication.java
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── exception/
src/main/resources/
├── application.yml
└── data.sql
src/test/java/org/example/
├── controller/          # MockMvc tests
└── service/             # Unit tests (Mockito)

Tests

mvn test





Service tests: Mockito (business logic)



Controller tests: @WebMvcTest + MockMvc



Context test: @SpringBootTest

If Mockito fails on Java 25, set the test JVM in IntelliJ to JDK 17 or 21
(Run → Edit Configurations → JRE).

Stack





Spring Boot 3.3 / Spring Web / Spring Data JPA



H2 (in-memory)



Bean Validation



@ControllerAdvice for error responses



SLF4J logging

