package com.apimanagement.Environment.APIs;

import com.apimanagement.Environment.EnvironmentResource.Environment;
import com.apimanagement.Environment.EnvironmentResource.EnvironmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/environments")
public class EnvironmentApi {

    @Autowired
    private EnvironmentRepository repo;

    @PostMapping
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<Environment> create(@RequestBody @Valid Environment environment) {
        Environment savedEnv = repo.save(environment);
        URI productURI = URI.create("/environments/" + savedEnv.getId());
        return ResponseEntity.created(productURI).body(savedEnv);
    }

    @GetMapping
    @RolesAllowed({"ROLE_ADMIN","ROLE_USER","ROLE_OWNER"})
    public List<Environment> list() {
        return repo.findAll();
    }
   // @Query("SELECT * FROM ENVIRONMENTS WHERE STATUS = 'BUSY' SET STATUS = IF((BUSY_TILL < CURDATE()),'AVAILABLE','BUSY')")
    @GetMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_USER","ROLE_OWNER"})
    @ResponseBody
    public Optional<Environment> getEnv(@PathVariable Integer id){
        return repo.findById(id);
    }
    @PutMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER"})
    public ResponseEntity<Environment> update(@PathVariable Integer id , @RequestBody @Valid Environment environment){
       Environment upEnv = repo.getById(id);
        upEnv.setEnvName(environment.getEnvName());
        upEnv.setBusy_till(environment.getBusy_till());
        upEnv.setStatus(environment.getStatus());
        upEnv.setUser(environment.getUser());
        upEnv = repo.save(upEnv);
        URI productURI = URI.create("/environments/"+upEnv.getId());
        return ResponseEntity.created(productURI).body(upEnv);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public  String delete(@PathVariable Integer id)
    {   String msg="";
        if(repo.getById(id)!= null)
        {   msg=""+repo.getById(id).getEnvName();
            repo.deleteById(id);
            msg += " with id "+id +"is deleted";
        }
        else{
            msg += "Environment  with id "+id + "is not exist";
        }
       return msg;
    }

    }
