package com.apimanagement.Environment.APIs;

import com.apimanagement.Environment.EnvironmentResource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;


// REST API controller for managing the environment and their services
@RestController
@RequestMapping("/environments")
public class ServiceManagingAPI {
    @Autowired private EnvironmentRepository repo;
    @Autowired private ServiceRepository serRepo;
    @Autowired private EnvironmentService envSer;

//POST request for  adding the service under the environment id
    @PostMapping("/{id}/services")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Service> create(@PathVariable Integer id, @RequestBody @Valid Service service) {
        Environment Env = repo.getById(id);
        Service savedServ = serRepo.save(service);
        Env = envSer.addingService(id,service);
        URI productURI = URI.create("/environments/" + Env.getId()+"/services/"+savedServ.getServiceId());
        return ResponseEntity.created(productURI).body(savedServ);
    }

// GET request for retrieving the all services
    @GetMapping("/services")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER","ROLE_USER"})
    public  List<Service> getAllServices(){
        serRepo.updateService();
        return serRepo.findAll();
    }

// GET request for retrieving the all services mapped with the environment id
    @GetMapping("/{id}/services")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER","ROLE_USER"})
    public List<Service> listAllEnvServices(@PathVariable Integer id)
    {
        serRepo.updateService();
        return serRepo.allByEnvId(id);
    }

// GET request for retrieving the service with the id under the environment id
    @GetMapping("/{id}/services/{idd}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER","ROLE_USER"})
    public  Service getServiceByEnvId( @PathVariable Integer id,@PathVariable Integer idd)
    {   serRepo.updateService();
        return serRepo.tookByEnvId(id,idd);
    }

// GET request for retrieve the service with the id
    @GetMapping("/services/{idd}")
    @RolesAllowed({"ROLE_ADMIN","ROLE_OWNER","ROLE_USER"})
    public  Service getServiceById( @PathVariable Integer idd)
    {
        serRepo.updateService();
        return serRepo.getById(idd);
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
    public ResponseEntity<Service>  update (@PathVariable Integer id,@PathVariable Integer idd, @RequestBody @Valid Service service)
   {
       Service saveServ = serRepo.getById(idd);
       saveServ.setServiceName(service.getServiceName());
       saveServ.setStatus(service.getStatus());
       saveServ.setBusyTill(service.getBusyTill());
       saveServ.setCurrUser(service.getCurrUser());
       saveServ= serRepo.save(saveServ);
       URI productURI = URI.create("/environments/"+id+"/services/"+saveServ.getServiceId());
       return ResponseEntity.created(productURI).body(saveServ);

   }
   @DeleteMapping("/{id}/services/{idd}")
   @RolesAllowed("ROLE_ADMIN")
   public String delete(@PathVariable Integer id, @PathVariable Integer idd)
   {
        String msg="";
        try{
        serRepo.preDelete(id,idd);
        serRepo.deleteById(idd);
        msg += "Service with id "+idd +"is deleted";

        }catch (Exception e){
        msg += "Service  with id "+idd + "is not deleted";
        }

       return msg;
}


}
