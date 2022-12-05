package com.apimanagement.Environment.controller;

import java.net.URI;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import com.apimanagement.Environment.exception.ErrorObject;
import com.apimanagement.Environment.model.User;
import com.apimanagement.Environment.dto.UserDTO;
import com.apimanagement.Environment.repository.UserRepository;
import com.apimanagement.Environment.service.serviceimpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired
    private UserRepository urepo;
    @Autowired
    private UserServiceImpl service;


    // POST request to signup with User details

    @PostMapping("/signup")
    @PermitAll
    @ResponseBody
    public  ResponseEntity<?> addUser(@RequestBody @Valid User user) {

        try {

            if(urepo.existsByUsername(user.getUsername())){
                return  ResponseEntity.badRequest().body(new ErrorObject("Username Already Exist"));

            }
            else {

                // default role for user
                user.setUser_roles("ROLE_USER");
                User newUser;
                newUser = service.save(user);
                newUser = service.addingRole(user);
                //DTO for showing the user's non-confidential data
                URI uri = URI.create("/users/" + newUser.getId());
                UserDTO userDto = new UserDTO(newUser.getId(), newUser.getUsername());
                return ResponseEntity.created(uri).body(userDto);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorObject(e.getMessage()));
        }
    }

    //PUT request to update the User with their roles
    @PutMapping("/users/{id}" )
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> updateUserRoles(@PathVariable Integer id,@RequestBody @Valid User user) {

        try {
            User createdUser = urepo.getById(id);
            createdUser.setUser_roles(user.getUser_roles());
            createdUser = service.addingRole(createdUser);
            urepo.save(createdUser);
            URI uri = URI.create("/users/" + createdUser.getId());
            //DTO for showing the user's non-confidential data
            UserDTO userDto = new UserDTO(createdUser.getId(), createdUser.getUsername());
            return ResponseEntity.created(uri).body(userDto);
        }
        catch (Exception e){
            return  ResponseEntity.badRequest().body(new ErrorObject(e.getMessage()));
        }

    }
    //GET request to list all the users
    @GetMapping("/users")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> list_users() {

            return ResponseEntity.ok().body(urepo.findAll());

    }
    // Delete User
    @DeleteMapping("/users/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id)
    {   try {

        urepo.deleteById(id);
        return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.status(403).body(new ErrorObject("User with "+id+" not found"));
    }

    }


}
