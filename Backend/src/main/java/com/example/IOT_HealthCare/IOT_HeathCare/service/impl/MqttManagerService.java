package com.example.IOT_HealthCare.IOT_HeathCare.service.impl;


import com.example.IOT_HealthCare.IOT_HeathCare.handlers.SensorWebSocketHandler;
import com.example.IOT_HealthCare.IOT_HeathCare.repository.DataSensorRepository;
import com.example.IOT_HealthCare.IOT_HeathCare.service.DeviceStateService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttManagerService implements MqttCallback {

    private final MqttClient mqttClient;
    private final DataSensorServiceImpl dataSensorService;
    private final DeviceStateService deviceStateService;
    private final SensorWebSocketHandler webSocketHandler;

    @Autowired
    public MqttManagerService(MqttClient mqttClient,
                              DataSensorServiceImpl dataSensorService,
                              DeviceStateService deviceStateService,
                              SensorWebSocketHandler webSocketHandler) {
        this.mqttClient = mqttClient;
        this.dataSensorService = dataSensorService;
        this.deviceStateService = deviceStateService;
        this.webSocketHandler = webSocketHandler;
    }

    @PostConstruct
    public void init() {
        try {
            mqttClient.setCallback(this);
            mqttClient.subscribe("data/sensor");
            mqttClient.subscribe("state/device");
            System.out.println("✅ MQTT Manager đã kết nối các topics: data/sensor, state/device");
        } catch (MqttException e) {
            System.err.println("❌ Lỗi khi subscribe MQTT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("❌ Mất kết nối MQTT: " + cause.getMessage());
        webSocketHandler.sendMqttStatus(false, "Mất kết nối MQTT: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("📨 Nhận MQTT [" + topic + "]: " + payload);

        try {
            switch (topic) {
                case "data/sensor":
                    dataSensorService.handleSensorMessage(payload);
                    break;
                case "state/device":
                    deviceStateService.handleStateMessage(payload);
                    break;
                default:
                    System.out.println("⚠️ Topic không được hỗ trợ: " + topic);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi xử lý MQTT message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}