package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    //Declaration
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    //getter and setter
}
