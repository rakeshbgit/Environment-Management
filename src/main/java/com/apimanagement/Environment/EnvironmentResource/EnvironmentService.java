package com.apimanagement.Environment.EnvironmentResource;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
// Class for additional utilities
@Transactional
@org.springframework.stereotype.Service
public class EnvironmentService {
    @Autowired private EnvironmentRepository envRepo;

    // Constructing the relationship btw environment and its service
    public  Environment addingService(Integer enId, Service service)
    {
        Environment env = envRepo.getById(enId);
        env.addService(service);
        return env;
    }
}
