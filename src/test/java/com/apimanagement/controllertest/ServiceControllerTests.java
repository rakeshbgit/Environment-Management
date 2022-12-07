package com.apimanagement.controllertest;
import com.apimanagement.Environment.dto.AuthRequest;
import com.apimanagement.Environment.EnvironmentManageApplication;
import com.apimanagement.Environment.model.*;
import com.apimanagement.Environment.repository.EnvironmentRepository;
import com.apimanagement.Environment.repository.RoleRepository;
import com.apimanagement.Environment.repository.ServiceRepository;
import com.apimanagement.Environment.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.Filter;
import java.sql.Date;
import java.util.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EnvironmentManageApplication.class)
@AutoConfigureMockMvc
public class ServiceControllerTests {

    @Autowired MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;
    private MockMvc mvc;

    Date date = Date.valueOf("2022-12-05");


    @Autowired
    UserRepository userRepo;

    @Autowired EnvironmentRepository envRepo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired ServiceRepository serRepo;

    ObjectMapper objectMapper = new ObjectMapper();
    String adminToken = "", ownerToken = "", userToken = "";
    String admin = "admin@gmail.com";
    String user = "user@gmail.com";
    String owner = "owner@gmail.com";
    String password = "password";


    @Before
    public  void setup() throws Exception {

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        if(! roleRepo.existsByName("ROLE_ADMIN"))
        {
            roleRepo.save(new Role("ROLE_ADMIN"));
        }
        if(! roleRepo.existsByName("ROLE_OWNER"))
        {
            roleRepo.save(new Role("ROLE_OWNER"));
        }

        User newUser = new User(admin, password);
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))).andExpect(status().isCreated());
        Role r = roleRepo.findByName("ROLE_ADMIN");
        roleRepo.addRole(userRepo.findByUsername(admin).get().getId(),r.getId());


        AuthRequest auth = new AuthRequest(admin, password);
        MvcResult mvc = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth))).andReturn();
        adminToken = mvc.getResponse().getContentAsString().split(",")[1].split(":")[1].replaceAll("}", " ").replaceAll("\"", " ").trim().toString();


        newUser = new User(user,password);
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))).andExpect(status().isCreated());
        auth = new AuthRequest(user,password);
        mvc = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth))).andReturn();
        userToken = mvc.getResponse().getContentAsString().split(",")[1].split(":")[1].replaceAll("}"," ").replaceAll("\""," ").trim().toString();


        newUser = new User(owner,password);
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))).andExpect(status().isCreated());
        r = roleRepo.findByName("ROLE_OWNER");
        roleRepo.addRole(userRepo.findByUsername(owner).get().getId(),r.getId());

        auth = new AuthRequest(owner,password);
        mvc = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth))).andReturn();
        ownerToken = mvc.getResponse().getContentAsString().split(",")[1].split(":")[1].replaceAll("}"," ").replaceAll("\""," ").trim().toString();

        //System.out.println(adminToken);



    }
    @After
    public void tearDown() {
        if (envRepo.existByEnvName("testEnvironment")) {
            envRepo.deleteById(envRepo.getByEnvName("testEnvironment"));
        }
        if (envRepo.existByEnvName("testEnvironmentUpdated")) {
            envRepo.deleteById(envRepo.getByEnvName("testEnvironmentUpdated"));
        }
        if(serRepo.existsByServiceName("testService")){
            serRepo.deleteByServiceName("testService");
        }

        User newUser = userRepo.findByUsername(user).get();
        roleRepo.deleteByUserID(newUser.getId());
        userRepo.delete(newUser);
        userToken="";

        newUser = userRepo.findByUsername(owner).get();
        roleRepo.deleteByUserID(newUser.getId());
        userRepo.delete(newUser);
        ownerToken = "";

        newUser = userRepo.findByUsername(admin).get();
        roleRepo.deleteByUserID(newUser.getId());
        userRepo.delete(newUser);
        adminToken = "";
    }

    @Test
    @Order(1)
    public void testCreateServiceWithValidToken()throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(post("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated());
    }
    @Test
    @Order(2)
    public void testCreateServiceWithInvalidToken() throws Exception
    {
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(post("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization", "Bearer "+userToken)).andExpect(status().isForbidden());

    }
    @Test
    @Order(3)
    public void testCreateServiceWithDuplicate()throws Exception
    {
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(post("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated());
        mockMvc.perform(post("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isBadRequest());

    }
    @Test
    @Order(4)
    public void testGetServicesByEnvironmentIdWithValid()throws  Exception{

        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(get("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+userToken)).andExpect(status().isOk());

    }
    @Test
    @Order(5)
    public void testGetServicesByEnvironmentIdWithINValid()throws  Exception{

        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(get("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+userToken+"ADDIssue")).andExpect(status().isUnauthorized());

    }
    @Test
    @Order(6)
    public void testGetServicesAvailableIdWithValid()throws  Exception{

        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(get("/environments/{id}/services/available",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+userToken)).andExpect(status().isOk());

    }
    @Test
    @Order(7)
    public void testModifyServiceWithValid() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(post("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated());
        newSer = new Service("testService",date ,"TestUser");
        Integer serId = serRepo.getByName("testService").getServiceId();
        mockMvc.perform(put("/environments/{id}/services/{idd}",enId,serId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization","Bearer "+ownerToken)).andExpect(status().isCreated());


    }
    @Test
    @Order(8)
    public void testDeleteServiceWithValidToken() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(post("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated());
        newSer = new Service("testService",date ,"TestUser");
        Integer serId = serRepo.getByName("testService").getServiceId();
        mockMvc.perform(delete("/environments/{id}/services/{idd}",enId,serId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+adminToken)).andExpect(status().isOk());

    }
    @Test
    @Order(9)
    public void testDeleteServiceWithInValidToken() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer enId = envRepo.getByEnvName(environmentName);
        Service newSer = new Service("testService");
        mockMvc.perform(post("/environments/{id}/services",enId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSer))
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated());
        newSer = new Service("testService",date ,"TestUser");
        Integer serId = serRepo.getByName("testService").getServiceId();
        mockMvc.perform(delete("/environments/{id}/services/{idd}",enId,serId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+userToken)).andExpect(status().isForbidden());

    }


}
