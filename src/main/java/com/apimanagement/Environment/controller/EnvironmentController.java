package com.apimanagement.Environment.controller;

import com.apimanagement.Environment.model.Environment;
import com.apimanagement.Environment.repository.EnvironmentRepository;
import com.apimanagement.Environment.model.Service;
import com.apimanagement.Environment.repository.ServiceRepository;
import com.apimanagement.Environment.exception.ErrorObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class EnvironmentController {

    @Autowired
    private EnvironmentRepository repo;
    @Autowired private ServiceRepository serRepo;

// Post request for Adding the environment with its name
    @PostMapping
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER"})


        public ResponseEntity<?> create(@RequestBody @Valid Environment environment){

        try{  Environment savedEnv = repo.save(environment);
            URI productURI = URI.create("/environments/" + savedEnv.getId());
            return ResponseEntity.created(productURI).body(savedEnv);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorObject("Unable to add new Environment "+ environment.getEnvName()+" "));
        }

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
    public ResponseEntity<?> getEnv(@PathVariable Integer id){
        try {
            return ResponseEntity.ok().body(repo.getById(id));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new ErrorObject("Environment with "+id+" is not found"));
        }
    }

// PUT request for updating the environment by id
    @PutMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER"})
    public ResponseEntity<?> update(@PathVariable Integer id , @RequestBody @Valid Environment environment) {
        try {
            Environment upEnv = repo.getById(id);
            upEnv.setEnvName(environment.getEnvName());
            upEnv = repo.save(upEnv);
            URI productURI = URI.create("/environments/" + upEnv.getId());
            return ResponseEntity.accepted().body(upEnv);
        }
        catch (Exception e){
            return ResponseEntity.status(403).body(new ErrorObject("Unable to update environment"));
        }
    }

// DELETE request to delete the environment and its services by id
    @DeleteMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN"})
    public  ResponseEntity<?> delete(@PathVariable Integer id)
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
            return  ResponseEntity.status(HttpStatus.ACCEPTED).body(new ErrorObject(msg,"200","Request Success"));
        }
        catch (Exception e){
        msg += "Error=> Environment not found or unable to delete :Environment with id "+id+" and its services ";
         return  ResponseEntity.status(403).body(new ErrorObject(msg));

         }

    }

    }
