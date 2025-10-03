package com.example.IOT_HealthCare.IOT_HeathCare.service;


import com.example.IOT_HealthCare.IOT_HeathCare.entities.DataSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface DataSensorService {
    void saveTemperature(double temperature);


    void handleSensorMessage(String payload);

    public Page<DataSensor> findAll(Pageable pageable);
    Page<DataSensor> findByNumericColumnAndValueRange(String column, double minValue, double maxValue, Pageable pageable);
    Page<DataSensor> findByTimeRange(Date startTime, Date endTime, Pageable pageable);
    Page<DataSensor> findByNumericColumnAndExactValue(String column, double value, Pageable pageable);
    //Page<DataSensor> findByExactTime(Date exactTime, Pageable pageable);
    Page<DataSensor> findByTimePattern(String timePattern, String patternType, Pageable pageable);
}