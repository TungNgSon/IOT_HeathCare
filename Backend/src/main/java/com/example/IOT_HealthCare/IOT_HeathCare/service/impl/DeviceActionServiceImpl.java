package com.example.IOT_HealthCare.IOT_HeathCare.service.impl;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DeviceAction;
import com.example.IOT_HealthCare.IOT_HeathCare.repository.DeviceActionRepository;
import com.example.IOT_HealthCare.IOT_HeathCare.service.DeviceActionService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeviceActionServiceImpl implements DeviceActionService {

    private final MqttClient mqttClient;
    private final DeviceActionRepository deviceActionRepository;

    public DeviceActionServiceImpl(MqttClient mqttClient, DeviceActionRepository deviceActionRepository) {
        this.mqttClient = mqttClient;
        this.deviceActionRepository = deviceActionRepository;
    }

    @Override
    public void controlLed(int id, String state) throws Exception {
        if (!mqttClient.isConnected()) {
            throw new RuntimeException("MQTT client is not connected!");
        }

        String msg = "LED" + id + " " + state.toUpperCase();
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(0);
        message.setRetained(false);

        mqttClient.publish("device/action", message);
        System.out.println("📤 Published to device/action: " + msg);
    }
    @Override
    public Page<DeviceAction> getAllDeviceActions(Pageable pageable) {
        return deviceActionRepository.findAll(pageable);
    }

    @Override
    public Page<DeviceAction> searchDeviceActions(
            String column,
            Integer minId,
            Integer maxId,
            Date startTime,
            Date endTime,
            String device,
            String action,
            Pageable pageable) {

        switch (column) {
            case "id":
                return deviceActionRepository.findByIdBetween(minId, maxId, pageable);
            case "time":
                return deviceActionRepository.findByTimeBetween(startTime, endTime, pageable);
            case "device":
                return deviceActionRepository.findByDevice(device, pageable);
            case "action":
                return deviceActionRepository.findByAction(action, pageable);
            default:
                return deviceActionRepository.findAll(pageable); // fallback
        }
    }

    @Override
    public Page<DeviceAction> findByColumnAndExactValue(String column, String value, Pageable pageable) {
        try {
            return deviceActionRepository.findByColumnAndExactValue(column, value, pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo giá trị cụ thể: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<DeviceAction> findByExactTime(Date exactTime, Pageable pageable) {
        try {
            return deviceActionRepository.findByExactTime(exactTime, pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo thời gian chính xác: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<DeviceAction> findByTimePattern(String timePattern, String patternType, Pageable pageable) {
        try {
            return deviceActionRepository.findByTimePattern(timePattern, patternType, pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo pattern thời gian: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<DeviceAction> searchByTimeValue(String timeValue, Pageable pageable) {
        try {
            String[] patternInfo = parseTimeInput(timeValue);
            if (patternInfo == null) {
                throw new IllegalArgumentException("Định dạng thời gian không hợp lệ: " + timeValue);
            }

            return findByTimePattern(patternInfo[0], patternInfo[1], pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo thời gian: " + e.getMessage());
            return Page.empty(pageable);
        }
    }



    private String[] parseTimeInput(String timeInput) {
        timeInput = timeInput.trim().replace("T", " ");

        if (timeInput.matches("\\d{4}")) {
            return new String[]{timeInput, "year"};
        } else if (timeInput.matches("\\d{4}-\\d{2}")) {
            return new String[]{timeInput, "month"};
        } else if (timeInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return new String[]{timeInput, "day"};
        } else if (timeInput.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}")) {
            return new String[]{timeInput, "hour"};
        } else if (timeInput.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            return new String[]{timeInput, "minute"};
        } else if (timeInput.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return new String[]{timeInput, "second"};
        }

        return null;
    }

    @Override
    public DeviceAction getLatestActionByDevice(String device) {
        return deviceActionRepository.findLatestActionByDevice(device.toUpperCase());
    }

    @Override
    public Page<DeviceAction> findByMultipleFiltersWithTimePattern(String device, String action, String timeValue, Pageable pageable) {
        try {
            if (timeValue == null || timeValue.trim().isEmpty()) {
                // Không có time filter, chỉ filter theo device và action
                return deviceActionRepository.findByMultipleFiltersWithTimePattern(device, action, null, null, null, pageable);
            }

            // Parse time input để xác định pattern type
            String[] patternInfo = parseTimeInput(timeValue.trim());
            if (patternInfo == null) {
                throw new IllegalArgumentException("Định dạng thời gian không hợp lệ: " + timeValue);
            }

            String timePattern = patternInfo[0];
            String patternType = patternInfo[1];
            Date exactTime = null;

            // Nếu là exact time (có đầy đủ giờ:phút:giây)
            if (patternType.equals("second")) {
                try {
                    // Parse thành Date object cho exact match
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    exactTime = sdf.parse(timePattern);
                    patternType = "exact";
                } catch (Exception e) {
                    // Fallback to pattern matching
                    patternType = "second";
                }
            }

            return deviceActionRepository.findByMultipleFiltersWithTimePattern(device, action, timePattern, patternType, exactTime, pageable);

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm multi-filter với time pattern: " + e.getMessage());
            return Page.empty(pageable);
        }
    }
}
