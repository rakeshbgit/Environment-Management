package com.apimanagement.Environment.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
//Model roles for Entity Role to manage user roles in the application
@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;
    //default Constructor
    public Role() { }
    // Parameterized constructor to initialize with role name
    public Role(String name) {
        this.name = name;
    }
    // Parameterized constructor to initialize with role id
    public Role(Integer id) {
        super();
        this.id = id;
    }
    // getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString() for showing Roles
    @Override
    public String toString() {
        return this.name;
    }


}