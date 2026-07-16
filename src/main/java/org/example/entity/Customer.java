package org.example.entity;

import com.sun.org.apache.xpath.internal.objects.XString;

public class Customer {
    //Declaration
    int id;
    String name;
    String email;

    //Constructor + Variable initialization
    public Customer(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;

    }
}
