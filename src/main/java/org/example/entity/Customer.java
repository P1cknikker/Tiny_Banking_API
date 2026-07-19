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

    private @Column(nullable = false)
    String email;

    //Constructor + Variable initialization
    public Customer(Long id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;

    }
}
