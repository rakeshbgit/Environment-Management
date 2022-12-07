package com.apimanagement.Environment.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.*;
import javax.transaction.Transactional;

@Embeddable
public class UsersRoles {

    public UsersRoles() {


    }


}
