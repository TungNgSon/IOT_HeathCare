package com.example.IOT_HealthCare.IOT_HeathCare.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "data_sensor") // Đảm bảo tên này khớp với tên bảng trong MySQL
@Getter
@Setter
public class DataSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "body_temperature") // Đảm bảo tên này khớp với tên cột trong MySQL
    private double bodyTemperature;

    @Column(name = "heart_rate") // Đảm bảo tên này khớp với tên cột trong MySQL
    private double heartRate;

    @Column(name = "SPO2") // Đảm bảo tên này khớp với tên cột trong MySQL
    private double SPO2;

    @Column(name = "time") // Đảm bảo tên này khớp với tên cột trong MySQL
    private Date time;
}