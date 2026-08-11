Tiny Banking API

Kleine Backend-Anwendung mit Spring Boot zur Verwaltung von Kunden, Konten und Buchungen.

Sprachen: English · Deutsch

Voraussetzungen





JDK 17 (oder 21; Tests idealerweise nicht mit Java 25)



Maven 3.8+



Optional: IntelliJ IDEA

Start

mvn spring-boot:run

Oder in IntelliJ: BankingApplication starten.







Ressource



URL





API



http://localhost:8080





H2-Console



http://localhost:8080/h2-console

H2-Login





JDBC-URL: jdbc:h2:mem:bankingdb



User: sa



Passwort: (leer)

Beim Start werden Beispieldaten aus data.sql geladen (2 Kunden, 3 Konten, 4 Transaktionen).

API-Endpunkte

Kunden







Methode



Pfad



Beschreibung





GET



/customers



Alle Kunden





POST



/customers



Kunde anlegen





GET



/customers/{id}



Kunde nach ID





PUT



/customers/{id}



Kunde aktualisieren





DELETE



/customers/{id}



Kunde löschen





GET



/customers/{id}/accounts



Konten eines Kunden

Konten & Buchungen







Methode



Pfad



Beschreibung





GET



/accounts



Alle Konten





POST



/accounts



Konto anlegen





GET



/accounts/{id}



Konto nach ID





POST



/accounts/{id}/deposit



Einzahlen





POST



/accounts/{id}/withdraw



Auszahlen





GET



/accounts/{id}/transactions



Transaktionen eines Kontos

Beispiel-Requests

# Kunden
curl http://localhost:8080/customers

curl -X POST http://localhost:8080/customers \
-H "Content-Type: application/json" \
-d "{\"name\":\"Lisa\",\"email\":\"lisa@test.de\"}"

# Konto
curl -X POST http://localhost:8080/accounts \
-H "Content-Type: application/json" \
-d "{\"iban\":\"DE89370400440532013000\",\"customerId\":1}"

# Einzahlen / Abheben
curl -X POST http://localhost:8080/accounts/1/deposit \
-H "Content-Type: application/json" \
-d "{\"amount\":100.50}"

curl -X POST http://localhost:8080/accounts/1/withdraw \
-H "Content-Type: application/json" \
-d "{\"amount\":20}"

# Kontostand & Transaktionen
curl http://localhost:8080/accounts/1
curl http://localhost:8080/accounts/1/transactions

Geschäftsregeln





Ein Kunde kann mehrere Konten haben



Auszahlung nur bei ausreichendem Guthaben



Betrag muss ≥ 0,01 sein



IBAN und E-Mail müssen eindeutig sein



Nicht gefundene Ressourcen → 404



Kunden mit vorhandenen Konten können nicht gelöscht werden → 409

Projektstruktur

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
├── controller/          # MockMvc-Tests
└── service/             # Unit-Tests (Mockito)

Tests

mvn test





Service-Tests: Mockito (Business-Logik)



Controller-Tests: @WebMvcTest + MockMvc



Context-Test: @SpringBootTest

Falls Mockito unter Java 25 scheitert: Test-JVM in IntelliJ auf JDK 17 oder 21 stellen
(Run → Edit Configurations → JRE).

Technik





Spring Boot 3.3 / Spring Web / Spring Data JPA



H2 (In-Memory)



Bean Validation



@ControllerAdvice für Fehlerantworten



SLF4J-Logging

