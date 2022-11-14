package com.apimanagement.Environment.APIs;

import java.net.URI;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import com.apimanagement.Environment.User.User;
import com.apimanagement.Environment.User.UserDTO;
import com.apimanagement.Environment.User.UserRepository;
import com.apimanagement.Environment.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserApi {
    @Autowired
    private UserRepository urepo;
    @Autowired
    private UserService service;

    @PutMapping("/users")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user) {
        user.setName(user.getUsername());
        User createdUser = service.save(user);
        URI uri = URI.create("/users/" + createdUser.getId());

        UserDTO userDto = new UserDTO(createdUser.getId(), createdUser.getName());

        return ResponseEntity.created(uri).body(userDto);
        //	return "";
    }
    @GetMapping("/users")
    @RolesAllowed("ROLE_ADMIN")
    public List<User> list_users() {

        return urepo.findAll();
    }

}

