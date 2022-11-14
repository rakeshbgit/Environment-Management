package com.apimanagement.Environment.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.awt.font.TextHitInfo;
import java.util.*;


@Entity
    @Table(name = "users")
    public class User implements UserDetails {
    //User - id, name, username(email),password,roles
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
        // Parametrized Constructor
        public  User(String username,String password){
            this.password = password;
            this.username = username;
            this.name =  username.substring(0,username.lastIndexOf('@'));

        }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", roles=" + roles +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
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
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
}
