package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Banking Application Integration Tests")
class BankingApplicationTest {

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        // Spring Boot startet den Context; Test schlägt fehl, wenn etwas nicht verdrahtet ist
    }
}
