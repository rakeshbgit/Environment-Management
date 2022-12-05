package com.apimanagement.Environment.controller;

import javax.validation.Valid;

import com.apimanagement.Environment.exception.ErrorObject;
import com.apimanagement.Environment.dto.AuthRequest;
import com.apimanagement.Environment.dto.AuthResponse;
import com.apimanagement.Environment.security.JwtTokenUtil;
import com.apimanagement.Environment.model.User;
import com.apimanagement.Environment.dto.UserDTO;
import com.apimanagement.Environment.service.serviceimpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// REST API controller for user authentication

@RestController
public class AuthController {
    @Autowired AuthenticationManager authManager;
    @Autowired
    JwtTokenUtil jwtUtil;
    @Autowired private UserServiceImpl servic;
    //Post the  request for user  to login
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);

            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorObject("Invalid Credentials","401","UNAUTHORIZED"));
        }
    }



}