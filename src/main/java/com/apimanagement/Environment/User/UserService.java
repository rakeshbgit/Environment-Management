package com.apimanagement.Environment.User;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@Transactional
public class UserService {
    @Autowired private UserRepository repo;
    @Autowired private PasswordEncoder passwordEncoder;

    public User save(User user) {
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        return repo.save(user);
    }
}
