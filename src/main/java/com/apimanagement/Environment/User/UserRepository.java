package com.apimanagement.Environment.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

     Optional <User> findByUsername( String username);

     // HQL Query to return to true if the user found with given argument value
     @Query("select case when count(u ) > 0 then true else false end from User u where username= :username")
     boolean existsByUsername(@Param("username")String username);
}
