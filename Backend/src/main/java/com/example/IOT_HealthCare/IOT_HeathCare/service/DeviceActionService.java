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

    // Exact search methods
    Page<DeviceAction> findByColumnAndExactValue(String column, String value, Pageable pageable);
    Page<DeviceAction> findByExactTime(Date exactTime, Pageable pageable);
    Page<DeviceAction> findByTimePattern(String timePattern, String patternType, Pageable pageable);
    Page<DeviceAction> searchByTimeValue(String timeValue, Pageable pageable);
    DeviceAction getLatestActionByDevice(String device);
    
    // Multi-filter search với time pattern support
    Page<DeviceAction> findByMultipleFiltersWithTimePattern(String device, String action, String timeValue, Pageable pageable);
}