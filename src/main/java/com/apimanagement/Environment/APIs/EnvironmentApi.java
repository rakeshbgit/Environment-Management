package com.apimanagement.Environment.APIs;

import com.apimanagement.Environment.EnvironmentResource.Environment;
import com.apimanagement.Environment.EnvironmentResource.EnvironmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

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
        repo.update();
        return repo.findAll();
    }
    @GetMapping("/available")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER"})
    public List<Environment>listAllAvailable(){
        return repo.findAllAvailableEnvironments();
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_USER","ROLE_OWNER"})
    @ResponseBody
    public Environment getEnv(@PathVariable Integer id){
        repo.update();
        return repo.getById(id);
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
    {
        String msg="";
        try{

       repo.deleteById(id);
            msg += "Environment with id "+id +"is deleted";

        }catch (Exception e){
            msg += "Environment  with id "+id + "is not exist";
        }

       return msg;
    }

    }
