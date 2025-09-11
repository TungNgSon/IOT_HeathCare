package com.example.IOT_HealthCare.IOT_HeathCare.controller;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DeviceAction;
import com.example.IOT_HealthCare.IOT_HeathCare.service.DeviceActionService;
import com.example.IOT_HealthCare.IOT_HeathCare.service.impl.DeviceActionServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/device")
class DeviceActionController {

    private final DeviceActionServiceImpl deviceActionServiceImpl;

    public DeviceActionController(DeviceActionServiceImpl deviceActionServiceImpl) {
        this.deviceActionServiceImpl = deviceActionServiceImpl;
    }

    // Ví dụ: GET /led/1/on
    @GetMapping("/{id}/{state}")
    public String controlLed(@PathVariable int id, @PathVariable String state) {
        try {
            deviceActionServiceImpl.controlLed(id, state);
            return "✅ LED" + id + " " + state.toUpperCase() + " command sent!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }
    @GetMapping("/page")
    public Page<DeviceAction> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return deviceActionServiceImpl.getAllDeviceActions(pageable);
    }

    @GetMapping("/search")
    public Page<DeviceAction> search(
            @RequestParam String column,
            @RequestParam(required = false) Integer minId,
            @RequestParam(required = false) Integer maxId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endTime,
            @RequestParam(required = false) String device,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return deviceActionServiceImpl.searchDeviceActions(column, minId, maxId, startTime, endTime, device, action, pageable);
    }
}
