package com.apimanagement.controllertest;


import com.apimanagement.Environment.dto.AuthRequest;
import com.apimanagement.Environment.EnvironmentManageApplication;
import com.apimanagement.Environment.dto.UserDTO;
import com.apimanagement.Environment.model.Environment;
import com.apimanagement.Environment.model.UsersRoles;
import com.apimanagement.Environment.repository.EnvironmentRepository;
import com.apimanagement.Environment.model.Role;
import com.apimanagement.Environment.model.User;
import com.apimanagement.Environment.repository.RoleRepository;
import com.apimanagement.Environment.repository.UserRepository;
import com.apimanagement.Environment.service.serviceimpl.UserServiceImpl;
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

import javax.servlet.Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EnvironmentManageApplication.class)
@AutoConfigureMockMvc
public class EnvironmentControllerTests {


    @Autowired MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mvc;

    @Autowired
    UserRepository userRepo;

    @Autowired
    EnvironmentRepository envRepo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired
    UserServiceImpl service;

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
    public void testCreateEnvironmentWithValid() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();


    }
    @Test
    @Order(2)
    public void testCreateEnvironmentWithInValid() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+userToken)).andExpect(status().isForbidden()).andReturn();


    }
    @Test
    @Order(3)
    public void testCreateEnvironmentWithDuplicate() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();

        mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isBadRequest()).andReturn();


    }

    @Test
    @Order(4)
    public void testAllEnvironmentsWithValidToken() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/environments").header("Authorization","Bearer "+userToken)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
    }
    @Test
    @Order(5)
    public void testAllEnvironmentsWithInValidToken() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/environments").header("Authorization","Bearer "+userToken+"AddingIssue")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @Order(6)
    public void testGetEnvironmentByIdWithValidToken() throws Exception
    {   String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer envId= envRepo.getByEnvName(environmentName);
        mockMvc.perform(get("/environments/{id}",envId).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer "+userToken)).andExpect(status().isOk())
                .andExpect(jsonPath("id",is(envId))).andExpect(jsonPath("envName",is(environmentName)));
    }
    @Test
    @Order(6)
    public void testGetEnvironmentByIdWithInValidToken() throws Exception
    {   String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer envId= envRepo.getByEnvName(environmentName);
        mockMvc.perform(get("/environments/{id}",envId).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer "+userToken+"AddingIssuer")).andExpect(status().isUnauthorized());
    }
    @Test
    @Order(7)
    public  void testUpdateEnvironmentWithValidToken() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer envId= envRepo.getByEnvName(environmentName);

        mockMvc.perform(put("/environments/{id}",envId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Environment("testEnvironmentUpdated"))).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+ownerToken)).andExpect(status().isAccepted())
                .andExpect(jsonPath("envName",is("testEnvironmentUpdated")));

    }
    @Test
    @Order(8)
    public  void testUpdateEnvironmentWithInValidToken() throws Exception{
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer envId= envRepo.getByEnvName(environmentName);

        mockMvc.perform(put("/environments/{id}",envId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Environment("testEnvironmentUpdated"))).accept(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer "+ownerToken)).andExpect(status().isForbidden());
    }
    @Test
    @Order(9)
    public void testDeleteEnvironmentWithValidToken() throws  Exception
    {
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer envId= envRepo.getByEnvName(environmentName);

        mockMvc.perform(delete("/environments/{id}",envId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+adminToken)).andExpect(status().isAccepted());

    }
    @Test
    @Order(10)
    public void testDeleteEnvironmentWithInValidToken() throws  Exception
    {
        String environmentName = "testEnvironment";
        Environment newEnv =new Environment();
        newEnv.setEnvName(environmentName);
        MvcResult mvcResult = mockMvc.perform(post("/environments").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEnv)).accept(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+adminToken)).andExpect(status().isCreated()).andReturn();
        Integer envId= envRepo.getByEnvName(environmentName);

        mockMvc.perform(delete("/environments/{id}",envId).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+userToken)).andExpect(status().isForbidden());

    }


}
