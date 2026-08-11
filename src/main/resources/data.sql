INSERT INTO customers (id, name, email) VALUES
    (1, 'Max Mustermann', 'max@example.com'),
    (2, 'Erika Musterfrau', 'erika@example.com');

INSERT INTO accounts (id, iban, balance, customer_id) VALUES
    (1, 'DE89370400440532013000', 1500.00, 1),
    (2, 'DE44500105175407324931', 250.50, 1),
    (3, 'DE12500105170648489890', 0.00, 2);

INSERT INTO transactions (id, account_id, type, amount, timestamp) VALUES
    (1, 1, 'DEPOSIT', 1000.00, TIMESTAMP '2026-01-15 10:00:00'),
    (2, 1, 'DEPOSIT', 600.00, TIMESTAMP '2026-02-01 14:30:00'),
    (3, 1, 'WITHDRAW', 100.00, TIMESTAMP '2026-02-10 09:15:00'),
    (4, 2, 'DEPOSIT', 250.50, TIMESTAMP '2026-03-01 11:00:00');
