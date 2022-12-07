package com.apimanagement.controllertest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.apimanagement.Environment.dto.AuthRequest;
import com.apimanagement.Environment.EnvironmentManageApplication;
import com.apimanagement.Environment.dto.UserDTO;
import com.apimanagement.Environment.model.Role;
import com.apimanagement.Environment.model.User;
import com.apimanagement.Environment.model.UsersRoles;
import com.apimanagement.Environment.repository.RoleRepository;
import com.apimanagement.Environment.repository.UserRepository;
import com.apimanagement.Environment.service.serviceimpl.UserServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EnvironmentManageApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public  class UserControllerTests {
    @Autowired
    UserRepository userRepo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired
    UserServiceImpl service;
    @Autowired private  MockMvc mockMvc ;

    ObjectMapper objectMapper = new ObjectMapper();

    String adminToken = "", ownerToken = "", userToken = "";
    String admin = "admin@gmail.com";
    String user = "user@gmail.com";
    String owner = "owner@gmail.com";
    String password = "password";


    @Before
    public  void setup() throws Exception {
        if(! roleRepo.existsByName("ROLE_ADMIN"))
        {
            roleRepo.save(new Role("ROLE_ADMIN"));
        }
        if(! roleRepo.existsByName("ROLE_OWNER"))
        {
            roleRepo.save(new Role("ROLE_OWNER"));
        }

        User newUser = new User(admin, password);
        ResultActions resultActions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))).andExpect(status().isCreated());
        Role r = roleRepo.findByName("ROLE_ADMIN");
        roleRepo.addRole(userRepo.findByUsername(admin).get().getId(),r.getId());


        AuthRequest auth = new AuthRequest(admin,password);
        MvcResult mvc = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth))).andReturn();
        adminToken = mvc.getResponse().getContentAsString().split(",")[1].split(":")[1].replaceAll("}"," ").replaceAll("\""," ").trim().toString();

        newUser = new User(user,password);
        resultActions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))).andExpect(status().isCreated());
        auth = new AuthRequest(user,password);
        mvc = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth))).andReturn();
        userToken = mvc.getResponse().getContentAsString().split(",")[1].split(":")[1].replace('}',' ').replace('"',' ').trim();

        newUser = new User(owner,password);
        resultActions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))).andExpect(status().isCreated());
        auth = new AuthRequest(owner,password);
        mvc = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth))).andReturn();
        ownerToken = mvc.getResponse().getContentAsString().split(",")[1].split(":")[1].replace('}',' ').replace('"',' ').trim();
        //System.out.println(adminToken);


    }
    @After
    public void teardown() throws Exception {

        if (userRepo.existsByUsername("testuser@gmail.com")) {
            User newUser = userRepo.findByUsername("testuser@gmail.com").get();
            roleRepo.deleteByUserID(newUser.getId());
            userRepo.delete(newUser);
        }


            User newUser = userRepo.findByUsername(user).get();
            roleRepo.deleteByUserID(newUser.getId());
            userRepo.delete(newUser);

            newUser = userRepo.findByUsername(owner).get();
            roleRepo.deleteByUserID(newUser.getId());
            userRepo.delete(newUser);

            newUser = userRepo.findByUsername(admin).get();
            roleRepo.deleteByUserID(newUser.getId());
            userRepo.delete(newUser);



    }


    @Test
    @Order(1)
    public void shouldCreateUser() throws JsonProcessingException, Exception {
        String username = "testuser@gmail.com";
        String password = "password123";
        User newUser = new User(username, password);
        //newUser.setName("daveKumar@gmail.com");
        //System.out.println(objectMapper.writeValueAsString(newUser));
        ResultActions resultActions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        resultActions.andExpect(status().isCreated());
        resultActions.andExpect(jsonPath("id", notNullValue()));
        resultActions.andExpect(jsonPath("username", is(username)));

        newUser = userRepo.findByUsername(username).get();
        roleRepo.deleteByUserID(newUser.getId());
        userRepo.delete(newUser);

    }
    @Test
    @Order(2)
    public  void  testLoginWithValidCredentials() throws Exception {
        AuthRequest auth = new AuthRequest(user,password);

        ResultActions resultActions = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth)));
        resultActions.andExpect(status().isOk());
        //resultActions.andDo(print());
        resultActions.andExpect(jsonPath("email",is(user)));
        resultActions.andExpect(jsonPath("accessToken",notNullValue()));



    }
    @Test
    @Order(3)
    public void testLoginWithInvalidUsername() throws Exception {
        AuthRequest auth = new AuthRequest("Invalid@gmail.com",password);

        ResultActions resultActions = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth)));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andExpect(jsonPath("errorMessage",is("Invalid Credentials")));
        resultActions.andExpect(jsonPath("status",is("UNAUTHORIZED")));

    }
    @Test
    @Order(4)
    public void testLoginWithInvalidPassword() throws Exception {
        AuthRequest auth = new AuthRequest(user,"abc123");

        ResultActions resultActions = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(auth)));
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andExpect(jsonPath("errorMessage",is("Invalid Credentials")));
        resultActions.andExpect(jsonPath("status",is("UNAUTHORIZED")));

    }
    @Test
    @Order(5)
    public void testAllUsersWithValidAccessToken() throws Exception {


        String token ="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyLHJha2VzaEBnbWFpbC5jb20iLCJpc3MiOiJDb2RlSmF2YSIsInJvbGVzIjoiW1JPTEVfQURNSU5dIiwiaWF0IjoxNjY5ODEwMDQ2LCJleHAiOjE2Njk4OTY0NDZ9.WrJsTqc1RkqFMZQeODBUM_Ga7igoz-4egaNcASN-xjDvjkkzMSzanulHrujbr4UWnScB7dJ4a2Kc24pHYKccgQ";

        // System.out.println(token);
        // System.out.println(jwtTokenFilter.getUserDetails(token));
        // System.out.println(adminToken);
        // System.out.println(jwtTokenFilter.getUserDetails(adminToken));
        MvcResult mvcResult = mockMvc.perform(
                        get("/users")
                                .header("Authorization","Bearer "+adminToken)).andExpect(status().isOk()).andReturn();


    }
    @Test
    @Order(6)
    public void testAllUsersWithInvalidToken() throws Exception {
        String token = "This is Invalid Token";


        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.get("/users")
                                .header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized()).andReturn();

    }
    @Test
    @Order(7)
    public void testAllUsersWithInvalidAccessToken() throws Exception {
        String token ="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyLHJha2VzaEBnbWFpbC5jb20iLCJpc3MiOiJDb2RlSmF2YSIsInJvbGVzIjoiW1JPTEVfQURNSU5dIivhgvcfgvgwiaWF0IjoxNjY5ODEwMDQ2LCJleHAiOjE2Njk4OTY0NDZ9.WrJsTqc1RkqFMZQeODBUM_Ga7igoz-4egaNcASN-xjDvjkkzMSzanulHrujbr4UWnScB7dJ4a2Kc24pHYKccgQ";
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/users")
                        .header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized()).andReturn();


    }
    @Test
    @Order(8)
    public void testDeleteUserWithValidToken()throws Exception{
        String username = "testuser@gmail.com";
        String password = "password123";
        User newUser = new User(username, password);

        ResultActions resultActions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        resultActions.andExpect(status().isCreated());
        Integer userId = userRepo.getByUsername(username).getId();
        MvcResult mvcResult = mockMvc.perform(delete("/users/{id}",userId)
                        .header("Authorization","Bearer "+adminToken)).andExpect(status().isOk()).andReturn();


    }
    @Test
    @Order(9)
    public void testDeleteUserWithInValidToken()throws Exception{
        String username = "testuser@gmail.com";
        String password = "password123";
        User newUser = new User(username, password);

        ResultActions resultActions = mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        resultActions.andExpect(status().isCreated());
        Integer userId = userRepo.getByUsername(username).getId();
        MvcResult mvcResult = mockMvc.perform(delete("/users/{id}",userId)
                .header("Authorization","Bearer "+userToken)).andExpect(status().isForbidden()).andReturn();
        roleRepo.deleteByUserID(userId);
        userRepo.deleteById(userId);





    }


}


