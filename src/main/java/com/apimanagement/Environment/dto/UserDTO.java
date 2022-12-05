package com.apimanagement.Environment.dto;

// Data Transfer Object for User
public class UserDTO {
    private Integer id;
    private String userName;

    public UserDTO() {
    }

    public UserDTO(Integer id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}


