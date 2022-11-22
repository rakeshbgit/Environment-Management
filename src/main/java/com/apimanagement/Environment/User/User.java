package com.apimanagement.Environment.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.awt.font.TextHitInfo;
import java.util.*;

// Model for User Entity
@Entity
    @Table(name = "users")
    public class User implements UserDetails {
    //User Entity - id, name, username(email),password,roles
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

    @Column(nullable = false, length = 50, unique = true)
        @Email
        private String username;

        @Column
        private String name;

        @Column(nullable = false, length = 64)
        private String password;

        private  String user_roles;
        //Table for managing user with their roles
    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
        private Set<Role> roles = new HashSet<>();
    //default Constructor
    public User(){

    }
    //parameterized Constructor for updating roles
    public  User(String username, String password, String user_roles)
    {
       // this.password = password;
        this.username = username;
        this.name =  username.substring(0,username.lastIndexOf('@'));

    }
    // Parameterized Constructor
    public  User(String username,String password){
        this.password = password;
        this.username = username;
        this.name =  username.substring(0,username.lastIndexOf('@'));

    }
    //
    public User(String user_roles){
        this.user_roles = user_roles;
    }

    // Respective getters and setters
    public String getUser_roles() {
        return user_roles;
    }

    public void setUser_roles(String user_roles) {
        this.user_roles = user_roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String uname) {
        this.name = uname.substring(0,uname.lastIndexOf('@'));
    }
    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
    // Overridden methods UserDetails for UserAccountManage
    // By default all are true when user added with credentials
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    // Adding the user's roles as authorities to manage authorization while accessing Application
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    // toString method for user details
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }
}
