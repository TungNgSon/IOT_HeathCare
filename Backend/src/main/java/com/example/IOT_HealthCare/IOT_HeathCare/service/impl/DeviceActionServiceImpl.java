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
}
