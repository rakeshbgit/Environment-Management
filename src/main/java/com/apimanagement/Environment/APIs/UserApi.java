package com.apimanagement.Environment.APIs;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import com.apimanagement.Environment.User.User;
import com.apimanagement.Environment.User.UserDTO;
import com.apimanagement.Environment.User.UserRepository;
import com.apimanagement.Environment.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserApi {
    @Autowired
    private UserRepository urepo;
    @Autowired
    private UserService service;

    // POST request to signup with User details

    @PostMapping("/signup")
    @PermitAll
    public  ResponseEntity<?> addUser(@RequestBody @Valid User user) {

        try {

            if(urepo.existsByUsername(user.getUsername())){
                return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
            }
            else {
                user.setName(user.getUsername());
                // default role for user
                user.setUser_roles("ROLE_USER");
                User newUser;
                newUser = service.save(user);
                newUser = service.addingRole(user);
                //DTO for showing the user's non-confidential data
                UserDTO userDto = new UserDTO(newUser.getId(), newUser.getName());
                return ResponseEntity.ok().body(userDto);
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //PUT request to update the User with their roles
    @PutMapping("/users/{id}" )
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> updateUserRoles(@PathVariable Integer id,@RequestBody @Valid User user) {

        User createdUser = urepo.getById(id);
        createdUser.setUser_roles(user.getUser_roles());
        createdUser = service.addingRole(createdUser);
        urepo.save(createdUser);
        URI uri = URI.create("/users/" + createdUser.getId());
        //DTO for showing the user's non-confidential data
        UserDTO userDto = new UserDTO(createdUser.getId(), createdUser.getName());
        return ResponseEntity.created(uri).body(userDto);

    }
    //GET request to list all the users
    @GetMapping("/users")
    @RolesAllowed("ROLE_ADMIN")
    public List<User> list_users() {

        return urepo.findAll();
    }

}

