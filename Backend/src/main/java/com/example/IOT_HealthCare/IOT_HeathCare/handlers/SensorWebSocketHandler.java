package com.example.IOT_HealthCare.IOT_HeathCare.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensorWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("🔗 WebSocket connected: " + session.getId() + " | Total sessions: " + sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("❌ WebSocket disconnected: " + session.getId() + " | Total sessions: " + sessions.size());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("📨 Received WebSocket message: " + message.getPayload());
        // Có thể xử lý message từ client nếu cần
    }

    // Gửi chỉ nhiệt độ
    public void sendTemperature(double temperature) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "temperature");
        data.put("value", temperature);
        data.put("unit", "°C");
        data.put("timestamp", System.currentTimeMillis());
        sendToAllSessions(data);
    }

    // Gửi chỉ nhịp tim
    public void sendHeartRate(int heartRate) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "heartRate");
        data.put("value", heartRate);
        data.put("unit", "BPM");
        data.put("timestamp", System.currentTimeMillis());
        sendToAllSessions(data);
    }

    // Gửi chỉ SpO2
    public void sendSpO2(int spO2) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "spO2");
        data.put("value", spO2);
        data.put("unit", "%");
        data.put("timestamp", System.currentTimeMillis());
        sendToAllSessions(data);
    }

    // Gửi tất cả dữ liệu sensor cùng lúc với thông tin database
    public void sendAllSensorData(int id, double heartRate, boolean hrValid, double spO2, boolean spo2Valid,
                                  double temperature, boolean tempValid, java.util.Date dbTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "allSensors");
        data.put("id", id); // ID từ database
        data.put("timestamp", System.currentTimeMillis());
        data.put("dbTime", dbTime.getTime()); // Thời gian lưu trong DB

        // Heart Rate data
        Map<String, Object> heartRateData = new HashMap<>();
        heartRateData.put("value", hrValid ? heartRate : null);
        heartRateData.put("valid", hrValid);
        heartRateData.put("unit", "BPM");
        data.put("heartRate", heartRateData);

        // SpO2 data
        Map<String, Object> spO2Data = new HashMap<>();
        spO2Data.put("value", spo2Valid ? spO2 : null);
        spO2Data.put("valid", spo2Valid);
        spO2Data.put("unit", "%");
        data.put("spO2", spO2Data);

        // Temperature data
        Map<String, Object> temperatureData = new HashMap<>();
        temperatureData.put("value", tempValid ? temperature : null);
        temperatureData.put("valid", tempValid);
        temperatureData.put("unit", "°C");
        data.put("temperature", temperatureData);

        sendToAllSessions(data);
    }

    // Overload method cho backward compatibility
    public void sendAllSensorData(double heartRate, boolean hrValid, double spO2, boolean spo2Valid,
                                  double temperature, boolean tempValid) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "allSensors");
        data.put("timestamp", System.currentTimeMillis());

        // Heart Rate data
        Map<String, Object> heartRateData = new HashMap<>();
        heartRateData.put("value", hrValid ? heartRate : null);
        heartRateData.put("valid", hrValid);
        heartRateData.put("unit", "BPM");
        data.put("heartRate", heartRateData);

        // SpO2 data
        Map<String, Object> spO2Data = new HashMap<>();
        spO2Data.put("value", spo2Valid ? spO2 : null);
        spO2Data.put("valid", spo2Valid);
        spO2Data.put("unit", "%");
        data.put("spO2", spO2Data);

        // Temperature data
        Map<String, Object> temperatureData = new HashMap<>();
        temperatureData.put("value", tempValid ? temperature : null);
        temperatureData.put("valid", tempValid);
        temperatureData.put("unit", "°C");
        data.put("temperature", temperatureData);

        sendToAllSessions(data);
    }

    // Gửi thông báo lỗi sensor
    public void sendSensorError(String sensorType, String errorMessage) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "error");
        data.put("sensor", sensorType);
        data.put("message", errorMessage);
        data.put("timestamp", System.currentTimeMillis());
        sendToAllSessions(data);
    }

    // Gửi trạng thái kết nối MQTT
    public void sendMqttStatus(boolean connected, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "mqttStatus");
        data.put("connected", connected);
        data.put("message", message);
        data.put("timestamp", System.currentTimeMillis());
        sendToAllSessions(data);
    }

    // Helper method để gửi data đến tất cả sessions
    private void sendToAllSessions(Object data) {
        if (sessions.isEmpty()) {
            System.out.println("⚠️ Không có WebSocket session nào để gửi dữ liệu");
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(data);
            TextMessage message = new TextMessage(json);

            // Dùng iterator để tránh ConcurrentModificationException
            sessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(message);
                        return false; // Giữ session
                    } else {
                        System.out.println("⚠️ Session đã đóng, xóa khỏi danh sách: " + session.getId());
                        return true; // Xóa session
                    }
                } catch (Exception e) {
                    System.err.println("❌ Lỗi gửi WebSocket message đến " + session.getId() + ": " + e.getMessage());
                    return true; // Xóa session lỗi
                }
            });

            System.out.println("📤 Đã gửi dữ liệu đến " + sessions.size() + " WebSocket sessions");

        } catch (Exception e) {
            System.err.println("❌ Lỗi serialize JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Getter để kiểm tra số lượng kết nối
    public int getActiveSessionCount() {
        return sessions.size();
    }

    // Method để broadcast thông báo tùy chỉnh
    public void broadcastMessage(String type, Object payload) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        data.put("payload", payload);
        data.put("timestamp", System.currentTimeMillis());
        sendToAllSessions(data);
    }

    // Method để gửi device state update
    public void sendDeviceStateUpdate(Map<String, Object> deviceStateData) {
        sendToAllSessions(deviceStateData);
    }
}