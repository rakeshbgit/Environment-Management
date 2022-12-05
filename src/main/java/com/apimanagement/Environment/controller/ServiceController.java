package com.apimanagement.Environment.controller;

import com.apimanagement.Environment.exception.ErrorObject;
import com.apimanagement.Environment.model.Environment;
import com.apimanagement.Environment.model.Service;
import com.apimanagement.Environment.repository.EnvironmentRepository;
import com.apimanagement.Environment.repository.ServiceRepository;
import com.apimanagement.Environment.service.serviceimpl.EnvironmentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import  java.text.DateFormat;
import java.util.List;


// REST API controller for managing the environment and their services
@RestController
@RequestMapping("/environments")
public class ServiceController {
    @Autowired private EnvironmentRepository repo;
    @Autowired private ServiceRepository serRepo;
    @Autowired private EnvironmentServiceImpl envSer;
    DateFormat today = new SimpleDateFormat("YYYY-MM-DD");

//POST request for  adding the service under the environment id
    @PostMapping("/{id}/services")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<?> create(@PathVariable Integer id, @RequestBody @Valid Service service) {

        try
        {
        Environment Env = repo.getById(id);
        service.setEnvironment_id(id);
        Service savedServ = serRepo.save(service);
        URI productURI = URI.create("/environments/" + Env.getId()+"/services/"+savedServ.getServiceId());
        return ResponseEntity.created(productURI).body(savedServ);}
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ErrorObject("Duplicate Entry with service name "+service.getServiceName()+" "+e.getLocalizedMessage()));
        }


    }



// GET request for retrieving the all services mapped with the environment id
    @GetMapping("/{id}/services")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER","ROLE_USER"})
    public List<Service> listAllEnvServices(@PathVariable Integer id)
    {
        serRepo.updateService();
        return serRepo.allByEnvId(id);
    }


// GET request for all available Services under the environment
    @GetMapping("/{id}/services/available")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER","ROLE_USER"})
    public List<Service> listAllByAvailable(@PathVariable Integer id)
    {
        serRepo.updateService();
        return serRepo.allByAvailable(id);
    }

// PUT request to update or modify the service by id
   @PutMapping("/{id}/services/{idd}")
   @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER"})
    public ResponseEntity<?>  update (@PathVariable Integer id,@PathVariable Integer idd, @RequestBody @Valid Service service)
   {
       try {
           Service saveServ = serRepo.getByServiceId(idd);
           saveServ.setEnvironment_id(id);
           saveServ.setServiceName(service.getServiceName());
           saveServ.setBusyTill(service.getBusyTill());
           if (!(service.getBusyTill().before(new Date()))) {
               saveServ.setStatus("BUSY");
           }
           saveServ.setCurrUser(service.getCurrUser());
           saveServ = serRepo.save(saveServ);
           URI productURI = URI.create("/environments/" + id + "/services/" + saveServ.getServiceId());
           return ResponseEntity.created(productURI).body(saveServ);
       }
       catch (Exception e){
           return  ResponseEntity.badRequest().body(e.getMessage());
       }

   }
   @DeleteMapping("/{id}/services/{idd}")
   @RolesAllowed("ROLE_ADMIN")
   @ResponseBody
   public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer idd)
   {
        String msg="";
        try{
        serRepo.preDelete(id,idd);
        serRepo.deleteByServiceId(idd);
        return ResponseEntity.ok().build();
        }
        catch (Exception e)
        {
         return ResponseEntity.badRequest().body(new ErrorObject(e.getMessage()));
        }


}


}
