package org.example.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(name = "accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = "iban"))
public class Account {
    //Declaration
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 34) //IBAN max
    private String iban;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    //getter and setter
}
