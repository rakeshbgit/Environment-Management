package com.apimanagement.Environment.User;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@Transactional
public class UserService {
    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private PasswordEncoder passwordEncoder;
// saving user with encoded password
    public User save(User user) {
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        return userRepo.save(user);

    }


    // adding a Role to UserRoles
    public User addingRole(User user){
        try{
           Role r = roleRepo.findByName(user.getUser_roles());
            user.addRole(new Role(r.getId()));
            user.setUser_roles("");

        }
        catch (Exception e){
         Role r =   roleRepo.save(new Role(user.getUser_roles()));
            user.addRole(new Role(r.getId()));
            user.setUser_roles("");

        }
       return userRepo.save(user);
    }
}
