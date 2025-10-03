package com.example.IOT_HealthCare.IOT_HeathCare.service.impl;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DeviceAction;
import com.example.IOT_HealthCare.IOT_HeathCare.handlers.SensorWebSocketHandler;
import com.example.IOT_HealthCare.IOT_HeathCare.repository.DeviceActionRepository;
import com.example.IOT_HealthCare.IOT_HeathCare.service.DeviceStateService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceStateServiceImpl implements DeviceStateService {

    private final DeviceActionRepository deviceActionRepository;
    private final SensorWebSocketHandler webSocketHandler;

    public DeviceStateServiceImpl(DeviceActionRepository deviceActionRepository, SensorWebSocketHandler webSocketHandler) {
        this.deviceActionRepository = deviceActionRepository;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void handleStateMessage(String message) {
        System.out.println("📥 Xử lý trạng thái thiết bị: " + message);

        if (message == null) {
            return;
        }

        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        // Expected format: "LED1 ON" or "LED2 OFF"
        String[] parts = trimmed.split("\\s+");
        if (parts.length < 2) {
            // Not a recognized state message
            return;
        }

        String device = parts[0];
        String action = parts[1];

        DeviceAction deviceAction = new DeviceAction();
        deviceAction.setDevice(device);
        deviceAction.setAction(action);
        deviceAction.setTime(new Date());

        deviceActionRepository.save(deviceAction);
        
        // Send device state update to frontend via WebSocket
        Map<String, Object> deviceStateData = new HashMap<>();
        deviceStateData.put("type", "deviceState");
        deviceStateData.put("device", device);
        deviceStateData.put("action", action);
        deviceStateData.put("timestamp", System.currentTimeMillis());
        
        webSocketHandler.sendDeviceStateUpdate(deviceStateData);
        System.out.println("📤 Sent device state update to frontend: " + device + " " + action);
    }
}
