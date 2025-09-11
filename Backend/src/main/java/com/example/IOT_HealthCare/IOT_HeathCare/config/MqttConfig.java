package com.example.IOT_HealthCare.IOT_HeathCare.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker.url:tcp://192.168.137.1:1883}")
    private String brokerUrl;

    @Value("${mqtt.client.id:spring-boot-subscriber}")
    private String clientId;

    @Value("${mqtt.username:user1}")
    private String username;

    @Value("${mqtt.password:123456}")
    private String password;

    @Bean
    public MqttClient mqttClient() throws MqttException {
        try {
            MqttClient client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            // Connection settings
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);

            // Retry logic for connection
            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    client.connect(options);
                    System.out.println("✅ MQTT Client connected successfully!");
                    System.out.println("✅ Broker: " + brokerUrl);
                    System.out.println("✅ Username: " + username);
                    break;
                } catch (MqttException e) {
                    retryCount++;
                    System.err.println("❌ Connection attempt " + retryCount + " failed: " + e.getMessage());

                    if (retryCount >= maxRetries) {
                        throw e;
                    }

                    // Wait before retry
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new MqttException(ie);
                    }
                }
            }

            return client;

        } catch (MqttException e) {
            System.err.println("❌ MQTT Connection failed completely!");
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("❌ Reason code: " + e.getReasonCode());

            // Log detailed error info
            if (e.getReasonCode() == 4) {
                System.err.println("❌ Bad username or password!");
            } else if (e.getReasonCode() == 3) {
                System.err.println("❌ Server unavailable!");
            }

            throw e;
        }
    }
}