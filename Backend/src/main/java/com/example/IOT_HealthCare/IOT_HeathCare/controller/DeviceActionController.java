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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/device")
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*", allowedHeaders = "*")
class DeviceActionController {

    private final DeviceActionServiceImpl deviceActionServiceImpl;

    public DeviceActionController(DeviceActionServiceImpl deviceActionServiceImpl) {
        this.deviceActionServiceImpl = deviceActionServiceImpl;
    }

    // Ví dụ: GET /led/1/on
    @GetMapping("/{id}/{state}")
    public ResponseEntity<String> controlLed(@PathVariable int id, @PathVariable String state) {
        try {
            deviceActionServiceImpl.controlLed(id, state);
            return ResponseEntity.ok("✅ LED" + id + " " + state.toUpperCase() + " command sent!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Lỗi tìm kiếm: " + e.getMessage());
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

//    @GetMapping("/search")
//    public Page<DeviceAction> search(
//            @RequestParam String column,
//            @RequestParam(required = false) Integer minId,
//            @RequestParam(required = false) Integer maxId,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date startTime,
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date endTime,
//            @RequestParam(required = false) String device,
//            @RequestParam(required = false) String action,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "time") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir) {
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
//        Pageable pageable = PageRequest.of(page, size, sort);
//        return deviceActionServiceImpl.searchDeviceActions(column, minId, maxId, startTime, endTime, device, action, pageable);
//    }

    @GetMapping("/search-exact")
    public ResponseEntity<Page<DeviceAction>> searchByExactValue(
            @RequestParam String column,
            @RequestParam(required = false) String value,
            @RequestParam(required = false) String timeValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Validate column
            if (!column.equals("id") && !column.equals("device") && !column.equals("action") && !column.equals("time")) {
                throw new IllegalArgumentException("Cột không hợp lệ: " + column);
            }

            // Create pageable object
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            // Handle time column
            if (column.equals("time")) {
                if (timeValue == null || timeValue.trim().isEmpty()) {
                    throw new IllegalArgumentException("timeValue là bắt buộc cho cột time");
                }
                return ResponseEntity.ok(deviceActionServiceImpl.searchByTimeValue(timeValue.trim(), pageable));
            }
            // Handle other columns
            else {
                if (value == null || value.trim().isEmpty()) {
                    throw new IllegalArgumentException("value là bắt buộc cho cột " + column);
                }
                return ResponseEntity.ok(deviceActionServiceImpl.findByColumnAndExactValue(column, value.trim(), pageable));
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi tìm kiếm: " + e.getMessage());
        }
    }
    @GetMapping("latest")
    public ResponseEntity<DeviceAction> getLatest(@RequestParam String device) {
        return  ResponseEntity.ok(deviceActionServiceImpl.getLatestActionByDevice(device));
    }


//    @GetMapping("/search-multi")
//    public ResponseEntity<Page<DeviceAction>> searchMulti(
//            @RequestParam(required = false) String device,
//            @RequestParam(required = false) String action,
//            @RequestParam(required = false) String timeValue,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "time") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir) {
//
//        try {
//            // Create pageable object
//            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
//            Pageable pageable = PageRequest.of(page, size, sort);
//
//            // Validate parameters
//            if (device != null && device.trim().isEmpty()) {
//                device = null;
//            }
//            if (action != null && action.trim().isEmpty()) {
//                action = null;
//            }
//            if (timeValue != null && timeValue.trim().isEmpty()) {
//                timeValue = null;
//            }
//
//            Page<DeviceAction> result = deviceActionServiceImpl.findByMultipleFiltersWithTimePattern(device, action, timeValue, pageable);
//            return ResponseEntity.ok(result);
//
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Lỗi tìm kiếm multi-filter: " + e.getMessage());
//        }
//    }

}
