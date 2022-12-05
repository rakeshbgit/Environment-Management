package com.apimanagement.Environment.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import java.util.HashSet;
import java.util.Set;

//Model environment for Entity Environment
@Entity
@Table(name="environment")
public class Environment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;
    @Column(unique = true)
    @NotNull @Length(min = 5, max = 128)
    private String envName;
    // Table environment_services having relation control over Environment and Services
    @OneToMany(mappedBy = "environment")

    private Set<Service> services = new HashSet<>();
    // Default Constructor
    public Environment(){

    }
    // Parameterized Constructor for initialize the environment with its name
    public Environment(String envName)
    {
        this.envName = envName;
    }

    //getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }
    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    public void addService(Service service) {
        this.services.add(service);
    }

    @Override
    public String toString() {
        return "Environment{" +
                "id=" + id +
                ", envName='" + envName + '\'' +
                ", services=" + services +
                '}';
    }
}
