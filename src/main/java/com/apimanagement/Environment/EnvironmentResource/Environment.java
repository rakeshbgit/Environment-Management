package com.apimanagement.Environment.EnvironmentResource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.hibernate.validator.constraints.Length;


@Entity
@Table(name="environment")
public class Environment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;
    @Column(nullable = false, length = 128)
    @NotNull @Length(min = 5, max = 128)
    private String envName;
    private  String status = "AVAILABLE";
    private String busy_till;
    private String user;
    public Environment(){

    }
    public Environment( String envName, String status,String busy_till,String user ){
        this.envName = envName;
        this.status = status;
        this.busy_till = busy_till;
        this.user = user;
    }
    public Environment(String envName)
    {
        this.envName = envName;
    }


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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBusy_till() {
        refresh();
        return busy_till;
    }

    public void setBusy_till(String busy_till) {
        this.busy_till = busy_till;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    public void refresh(){
        DateTimeFormatter dtobj = DateTimeFormatter.ofPattern("yyyy-mm-dd");
        if(!getStatus().equalsIgnoreCase("AVAILABLE")){
            LocalDate ldb = LocalDate.parse(getBusy_till(),dtobj);
            LocalDate ldt = LocalDate.parse(LocalDate.now().toString(),dtobj);
            if(!ldt.isAfter(ldb)){
                this.setStatus("AVAILABLE");
            }
        }
    }
}
