package com.example.IOT_HealthCare.IOT_HeathCare.controller;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DataSensor;
import com.example.IOT_HealthCare.IOT_HeathCare.service.impl.DataSensorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/sensors")
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class DataSensorController {

    @Autowired
    private DataSensorServiceImpl dataSensorService;


//    @GetMapping("/realtime")
//    public ResponseEntity<Map<String, Object>> getRealtimeSensorData() {
//        try {
//            Map<String, Object> response = new HashMap<>();
//
//            // Dữ liệu real-time từ memory
//            Map<String, Object> sensorData = new HashMap<>();
//            sensorData.put("heartRate", dataSensorService.getLastHeartRate());
//            sensorData.put("heartRateValid", dataSensorService.isHeartRateValid());
//            sensorData.put("spO2", dataSensorService.getLastSpO2());
//            sensorData.put("spO2Valid", dataSensorService.isSpO2Valid());
//            sensorData.put("temperature", dataSensorService.getLastTemperature());
//            sensorData.put("temperatureValid", dataSensorService.isTemperatureValid());
//            sensorData.put("timestamp", System.currentTimeMillis());
//
//            response.put("status", "success");
//            response.put("message", "Dữ liệu sensor real-time");
//            response.put("data", sensorData);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy dữ liệu real-time: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//
//    @GetMapping("/latest")
//    public ResponseEntity<Map<String, Object>> getLatestSensorData() {
//        try {
//            DataSensor latestData = dataSensorService.getLatestSensorData();
//
//            Map<String, Object> response = new HashMap<>();
//
//            if (latestData != null) {
//                response.put("status", "success");
//                response.put("message", "Dữ liệu sensor mới nhất từ database");
//                response.put("data", latestData);
//            } else {
//                response.put("status", "warning");
//                response.put("message", "Không có dữ liệu trong database");
//                response.put("data", null);
//            }
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy dữ liệu mới nhất: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//
//    @GetMapping("/today")
//    public ResponseEntity<Map<String, Object>> getTodaySensorData() {
//        try {
//            List<DataSensor> todayData = dataSensorService.getTodayData();
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Dữ liệu sensor hôm nay");
//            response.put("count", todayData.size());
//            response.put("data", todayData);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy dữ liệu hôm nay: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//
//    @GetMapping("/recent")
//    public ResponseEntity<Map<String, Object>> getRecentSensorData(
//            @RequestParam(defaultValue = "10") int limit) {
//        try {
//            // Giới hạn tối đa 100 records
//            if (limit > 100) limit = 100;
//            if (limit < 1) limit = 1;
//
//            List<DataSensor> recentData = dataSensorService.getRecentData(limit);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Dữ liệu sensor gần đây");
//            response.put("limit", limit);
//            response.put("count", recentData.size());
//            response.put("data", recentData);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy dữ liệu gần đây: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//
//    @GetMapping("/status")
//    public ResponseEntity<Map<String, Object>> getSensorStatus() {
//        try {
//            Map<String, Object> sensorStatus = new HashMap<>();
//            sensorStatus.put("heartRate", Map.of(
//                    "connected", dataSensorService.isHeartRateValid(),
//                    "lastValue", dataSensorService.getLastHeartRate(),
//                    "unit", "BPM"
//            ));
//            sensorStatus.put("spO2", Map.of(
//                    "connected", dataSensorService.isSpO2Valid(),
//                    "lastValue", dataSensorService.getLastSpO2(),
//                    "unit", "%"
//            ));
//            sensorStatus.put("temperature", Map.of(
//                    "connected", dataSensorService.isTemperatureValid(),
//                    "lastValue", dataSensorService.getLastTemperature(),
//                    "unit", "°C"
//            ));
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Trạng thái kết nối sensor");
//            response.put("timestamp", System.currentTimeMillis());
//            response.put("data", sensorStatus);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy trạng thái sensor: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//
//    @GetMapping("/heartrate")
//    public ResponseEntity<Map<String, Object>> getHeartRateData() {
//        try {
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Dữ liệu nhịp tim");
//            response.put("data", Map.of(
//                    "value", dataSensorService.getLastHeartRate(),
//                    "valid", dataSensorService.isHeartRateValid(),
//                    "unit", "BPM",
//                    "timestamp", System.currentTimeMillis()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy dữ liệu nhịp tim: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//
//    @GetMapping("/spo2")
//    public ResponseEntity<Map<String, Object>> getSpO2Data() {
//        try {
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Dữ liệu SpO2");
//            response.put("data", Map.of(
//                    "value", dataSensorService.getLastSpO2(),
//                    "valid", dataSensorService.isSpO2Valid(),
//                    "unit", "%",
//                    "timestamp", System.currentTimeMillis()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy dữ liệu SpO2: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//
//    @GetMapping("/temperature")
//    public ResponseEntity<Map<String, Object>> getTemperatureData() {
//        try {
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Dữ liệu nhiệt độ");
//            response.put("data", Map.of(
//                    "value", dataSensorService.getLastTemperature(),
//                    "valid", dataSensorService.isTemperatureValid(),
//                    "unit", "°C",
//                    "timestamp", System.currentTimeMillis()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("status", "error");
//            errorResponse.put("message", "Lỗi khi lấy dữ liệu nhiệt độ: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }


    @GetMapping("/last24h")
    public ResponseEntity<List<DataSensor>> getLast24HoursData() {

            List<DataSensor> last24hDataSensors=dataSensorService.findLast24HoursData();
            return ResponseEntity.ok(last24hDataSensors);

    }
    @GetMapping("/page")
    public ResponseEntity<Page<DataSensor>> getAllDataSensors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DataSensor> dataSensors = dataSensorService.findAll(pageable);

        return ResponseEntity.ok(dataSensors);
    }

    @GetMapping("/search-exact")
    public ResponseEntity<Page<DataSensor>> searchByExactValue(
            @RequestParam String column,
            @RequestParam(required = false) Double numericValue,
            @RequestParam(required = false) String timeValue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Validate column
            if (!column.equals("id") && !column.equals("heartRate") && !column.equals("SPO2") &&
                    !column.equals("bodyTemperature") && !column.equals("time")) {
                throw new IllegalArgumentException("Cột không hợp lệ: " + column);
            }

            // Create pageable object
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);

            // Handle numeric columns
            if (column.equals("id") || column.equals("heartRate") || column.equals("SPO2") ||
                    column.equals("bodyTemperature")) {
                if (numericValue == null) {
                    throw new IllegalArgumentException("numericValue là bắt buộc cho cột số");
                }
                return ResponseEntity.ok(dataSensorService.findByNumericColumnAndExactValue(column, numericValue, pageable));
            }
            // Handle time column
            else {
                if (timeValue == null || timeValue.trim().isEmpty()) {
                    throw new IllegalArgumentException("timeValue là bắt buộc cho cột time");
                }

                return ResponseEntity.ok(dataSensorService.searchByTimeValue(timeValue.trim(), pageable));
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi tìm kiếm: " + e.getMessage());
        }
    }



    // Xử lý lỗi bằng @ExceptionHandler
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}