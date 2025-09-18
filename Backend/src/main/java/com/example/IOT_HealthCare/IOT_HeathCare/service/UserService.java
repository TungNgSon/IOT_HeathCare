package com.example.IOT_HealthCare.IOT_HeathCare.service;

import com.example.IOT_HealthCare.IOT_HeathCare.dto.RequestRegisterUserDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.dto.RequestUserLoginDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.dto.ResponseUserLoginDTO;
import com.example.IOT_HealthCare.IOT_HeathCare.entities.User;

public interface UserService {
    public ResponseUserLoginDTO login(RequestUserLoginDTO dto);
    public ResponseUserLoginDTO register(RequestRegisterUserDTO dto);
}
