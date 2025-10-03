package com.example.IOT_HealthCare.IOT_HeathCare.controller;

import com.example.IOT_HealthCare.IOT_HeathCare.dto.RequestRegisterUserDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.dto.RequestUserLoginDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.dto.ResponseUserLoginDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.service.UserService;
import com.example.IOT_HealthCare.IOT_HeathCare.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private UserServiceImpl userServiceImpl;
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RequestRegisterUserDTO dto) {
        ResponseUserLoginDTO responseUserLoginDTO = userServiceImpl.register(dto);
        if(responseUserLoginDTO == null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        return ResponseEntity.ok(responseUserLoginDTO);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestUserLoginDTO dto) {
        ResponseUserLoginDTO responseUserLoginDTO = userServiceImpl.login(dto);
        if(responseUserLoginDTO == null) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        return ResponseEntity.ok(responseUserLoginDTO);


    }
}
