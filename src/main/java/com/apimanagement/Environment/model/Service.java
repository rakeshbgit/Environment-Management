package com.apimanagement.Environment.model;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;


//Model service for Entity  service with id, name , status, busyTill,user
@Entity
@Table(name="service")
public class Service {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer serviceId;
    @Column(unique = true)  @NotNull
    @Length(min = 5, max = 128)
    private String serviceName;
    @ManyToOne
    @JoinColumn( name = "environment_id",referencedColumnName = "id",nullable = false)
    private Environment environment;
    @Column
    private String status="AVAILABLE";
    @Column
    @DateTimeFormat
    private Date busyTill;
    @Column
    private String currUser;
    @Column
    private String lastUser;
    @Column(insertable = false,updatable = false)
    private  Integer environment_id;

    //default constructor
    public Service(){

    }



    // parameterized constructor for updating the service
    public Service(String name, Date busyTill,String user)
    {
        this.serviceName = name;
        this.busyTill = busyTill;
        this.currUser = user;

    }
    //parameterized constructor for creating the service
    public Service(String name){
        this.serviceName = name;
    }

    // getters and setters
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer sId) {
        this.serviceId = sId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getBusyTill() {
        return busyTill;
    }

    public void setBusyTill(Date busyTill) {
        this.busyTill = busyTill;
    }

    public String getCurrUser() {
        return currUser;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public String getLastUser() {
        return lastUser;
    }
    public Integer getEnvironment_id() {
        return environment_id;
    }

    public void setEnvironment_id(Integer environment_id) {
        this.environment_id = environment_id;
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
    }
    // toString() for showing data of the service
    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", status='" + status + '\'' +
                ", busyTill=" + busyTill +
                ", currUser='" + currUser + '\'' +
                ", lastUser='" + lastUser + '\'' +
                '}';
    }
}
