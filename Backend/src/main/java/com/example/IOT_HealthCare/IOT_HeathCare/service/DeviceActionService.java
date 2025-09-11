// DeviceActionService.java
package com.example.IOT_HealthCare.IOT_HeathCare.service;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DeviceAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Date;

public interface DeviceActionService {
    void controlLed(int id, String state) throws Exception;

    Page<DeviceAction> getAllDeviceActions(Pageable pageable);



    Page<DeviceAction> searchDeviceActions(
            String column,
            Integer minId,
            Integer maxId,
            Date startTime,
            Date endTime,
            String device,
            String action,
            Pageable pageable);
}