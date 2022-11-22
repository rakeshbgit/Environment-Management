package com.apimanagement.Environment.APIs;

import com.apimanagement.Environment.EnvironmentResource.Environment;
import com.apimanagement.Environment.EnvironmentResource.EnvironmentRepository;
import com.apimanagement.Environment.EnvironmentResource.Service;
import com.apimanagement.Environment.EnvironmentResource.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.*;

// REST API controller for Environment management
@RestController
// Requests are mapped from the base URI '/environments'
@RequestMapping("/environments")
public class EnvironmentApi {

    @Autowired
    private EnvironmentRepository repo;
    @Autowired private ServiceRepository serRepo;

// Post request for Adding the environment with its name
    @PostMapping
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER"})

    public ResponseEntity<Environment> create(@RequestBody @Valid Environment environment) {
        Environment savedEnv = repo.save(environment);
        URI productURI = URI.create("/environments/" + savedEnv.getId());
        return ResponseEntity.created(productURI).body(savedEnv);
    }
// Get request for retrieving all environments
    @GetMapping
    @RolesAllowed({"ROLE_ADMIN","ROLE_USER","ROLE_OWNER"})

    public List<Environment> list() {
        return repo.findAll();
    }

// GET request for retrieve the environment of provided id
    @GetMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_USER","ROLE_OWNER"})
    @ResponseBody
    public Environment getEnv(@PathVariable Integer id){

        return repo.getById(id);
    }

// PUT request for updating the environment by id
    @PutMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER"})
    public ResponseEntity<Environment> update(@PathVariable Integer id , @RequestBody @Valid Environment environment) {
        Environment upEnv = repo.getById(id);
        upEnv.setEnvName(environment.getEnvName());
        upEnv = repo.save(upEnv);
        URI productURI = URI.create("/environments/" + upEnv.getId());
        return ResponseEntity.created(productURI).body(upEnv);
    }

// DELETE request to delete the environment and its services by id
    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public  String delete(@PathVariable Integer id)
    {
       String msg="";

        try{
            List<Service> envServices = serRepo.allByEnvId(id);
            for(Service s : envServices){
                serRepo.preDelete(id,s.getServiceId());
                serRepo.delete(s);
            }
            repo.deleteById(id);
            msg += "Environment with id "+id+" and its services is deleted";
        }
        catch (Exception e){
        msg += "Error=> Environment not found or unable to delete :Environment with id "+id+" and its services ";

         }
        return msg;
    }

    }
