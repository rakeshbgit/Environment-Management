package com.apimanagement.Environment.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

//    Optional<User> findByEmail(String username);
     Optional<User> findByUsername(String username);

}
