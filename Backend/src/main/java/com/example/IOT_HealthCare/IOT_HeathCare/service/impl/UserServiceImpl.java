package com.example.IOT_HealthCare.IOT_HeathCare.service.impl;

import com.example.IOT_HealthCare.IOT_HeathCare.dto.RequestRegisterUserDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.dto.RequestUserLoginDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.dto.ResponseUserLoginDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.entities.User;
import com.example.IOT_HealthCare.IOT_HeathCare.repository.UserRepository;
import com.example.IOT_HealthCare.IOT_HeathCare.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public ResponseUserLoginDTO login(RequestUserLoginDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername());
        if (user != null && passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            ResponseUserLoginDTO responseDTO = new ResponseUserLoginDTO();
            responseDTO.setId(user.getId());
            responseDTO.setUsername(user.getUsername());

            return responseDTO;
        }
        return null;

    }
    @Override
    public ResponseUserLoginDTO register(RequestRegisterUserDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()) != null) {
            return null; // Username already exists
        }
        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        User savedUser = userRepository.save(newUser);
        ResponseUserLoginDTO responseDTO = new ResponseUserLoginDTO();
        responseDTO.setId(savedUser.getId());
        responseDTO.setUsername(savedUser.getUsername());
        return responseDTO;
    }
}
