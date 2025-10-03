#include <WiFi.h>
#include <PubSubClient.h>
#include <Wire.h>
#include "MAX30105.h"
#include "spo2_algorithm.h"
#include <OneWire.h>
#include <DallasTemperature.h>

// ====== WiFi ======
const char* ssid = "YOUR_WIFI_NAME";
const char* password = "YOUR_WIFI_PASSWORD";

// ====== MQTT ======
const char* mqtt_server = "IP_OF_MQTT_BROKER";
const int mqtt_port = 1883;
const char* mqtt_user = "YOUR_USERNAME";
const char* mqtt_pass = "YOUR_PASSWORD";
const char* topic_sensor = "data/sensor";
const char* topic_led = "device/action";
const char* topic_state = "state/device"; 

WiFiClient espClient;
PubSubClient client(espClient);

// ====== LED ======
const int ledPin1 = 26;
const int ledPin2 = 32;
const int ledPin3 = 12;

// ====== MAX30102 ======
MAX30105 particleSensor;
#define BUFFER_SIZE 100
uint32_t irBuffer[BUFFER_SIZE];
uint32_t redBuffer[BUFFER_SIZE];
int32_t spo2;
int8_t validSPO2;
int32_t heartRate;
int8_t validHeartRate;

// ====== DS18B20 ======
#define ONE_WIRE_BUS 33
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

// ====== Shared data với mutex ======
SemaphoreHandle_t dataMutex;
struct SensorData {
  int32_t heartRate;
  int8_t validHeartRate;
  int32_t spo2;
  int8_t validSPO2;
  float temperature;
  bool tempValid;
  unsigned long timestamp;
} sharedData;

// ====== Task handles ======
TaskHandle_t Task1;
TaskHandle_t Task2;

// ====== Callback MQTT ======
void callback(char* topic, byte* message, unsigned int length) {
  String msg;
  for (int i = 0; i < length; i++) msg += (char)message[i];

  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("]: ");
  Serial.println(msg);
  if (msg == "LED1 ON") {
    digitalWrite(ledPin1, HIGH);
    client.publish(topic_state, "LED1 ON");   // gửi response
  } 
  else if (msg == "LED1 OFF") {
    digitalWrite(ledPin1, LOW);
    client.publish(topic_state, "LED1 OFF");  // gửi response
  } 
  else if (msg == "LED2 ON") {
    digitalWrite(ledPin2, HIGH);
    client.publish(topic_state, "LED2 ON");   // gửi response
  } 
  else if (msg == "LED2 OFF") {
    digitalWrite(ledPin2, LOW);
    client.publish(topic_state, "LED2 OFF");  // gửi response
  }
  else if (msg == "LED3 ON") {
    digitalWrite(ledPin3, HIGH);
    client.publish(topic_state, "LED3 ON");  // gửi response
  }
  else if (msg == "LED3 OFF") {
    digitalWrite(ledPin3, LOW);
    client.publish(topic_state, "LED3 OFF");  // gửi response
  }

}


// ====== Kết nối WiFi ======
void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}

// ====== Kết nối MQTT ======
void reconnect() {
  while (!client.connected()) {
    Serial.print("Connecting to MQTT...");
    if (client.connect("ESP32Client", mqtt_user, mqtt_pass)) {
      Serial.println("connected");
      client.subscribe(topic_led);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(", retry in 5s");
      delay(5000);
    }
  }
}

// ====== TASK 1: Đọc cảm biến (Core 0) ======
void Task1code(void * pvParameters) {
  Serial.print("Task1 running on core ");
  Serial.println(xPortGetCoreID());

  for(;;) {
    // Đọc MAX30102
    for (int i = 0; i < BUFFER_SIZE; i++) {
      while (!particleSensor.available()) particleSensor.check();
      redBuffer[i] = particleSensor.getRed();
      irBuffer[i] = particleSensor.getIR();
      particleSensor.nextSample();
    }

    maxim_heart_rate_and_oxygen_saturation(
      irBuffer, BUFFER_SIZE,
      redBuffer,
      &spo2, &validSPO2,
      &heartRate, &validHeartRate
    );

    // Đọc DS18B20
    sensors.requestTemperatures();
    float temperatureC = sensors.getTempCByIndex(0);

    // Cập nhật shared data với mutex protection
    if (xSemaphoreTake(dataMutex, portMAX_DELAY) == pdTRUE) {
      sharedData.heartRate = heartRate;
      sharedData.validHeartRate = validHeartRate;
      sharedData.spo2 = spo2;
      sharedData.validSPO2 = validSPO2;
      sharedData.temperature = temperatureC;
      sharedData.tempValid = (temperatureC != DEVICE_DISCONNECTED_C);
      sharedData.timestamp = millis();
      xSemaphoreGive(dataMutex);
    }

    // Task delay để không chiếm hết CPU
    vTaskDelay(100 / portTICK_PERIOD_MS); // 100ms delay
  }
}

// ====== TASK 2: MQTT Communication (Core 1) ======
void Task2code(void * pvParameters) {
  Serial.print("Task2 running on core ");
  Serial.println(xPortGetCoreID());

  for(;;) {
    // MQTT check
    if (!client.connected()) reconnect();
    client.loop();

    // Đọc shared data và publish
    SensorData localData;
    if (xSemaphoreTake(dataMutex, pdMS_TO_TICKS(50)) == pdTRUE) {
      localData = sharedData;
      xSemaphoreGive(dataMutex);

      // Publish dữ liệu sensor
      char msg[150];
      snprintf(msg, sizeof(msg), "BPM: %s, SpO2: %s, Temp: %s",
               localData.validHeartRate ? String(localData.heartRate).c_str() : "N/A",
               localData.validSPO2 ? String(localData.spo2).c_str() : "N/A",
               localData.tempValid ? String(localData.temperature, 2).c_str() : "N/A");
      
      client.publish(topic_sensor, msg);

      // Serial log
      Serial.print("BPM: ");
      if (localData.validHeartRate) Serial.print(localData.heartRate); else Serial.print("N/A");
      Serial.print(" | SpO2: ");
      if (localData.validSPO2) Serial.print(localData.spo2); else Serial.print("N/A");
      Serial.print(" | Temp: ");
      if (localData.tempValid) Serial.println(localData.temperature); else Serial.println("N/A");
    }

    // Task delay
    vTaskDelay(1000 / portTICK_PERIOD_MS); // 1 second delay for publishing
  }
}

void setup() {
  Serial.begin(115200);

  // LED
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);
  pinMode(ledPin3, OUTPUT);
  

  // Tạo mutex cho shared data
  dataMutex = xSemaphoreCreateMutex();

  // WiFi + MQTT
  setup_wifi();
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);

  // MAX30102
  if (!particleSensor.begin(Wire, I2C_SPEED_STANDARD)) {
    Serial.println("Cannot find MAX30102!");
    while (1);
  }
  particleSensor.setup();
  particleSensor.setPulseAmplitudeRed(0x0A);
  particleSensor.setPulseAmplitudeGreen(0);

  // DS18B20
  sensors.begin();

  Serial.println("Setup done, creating tasks...");

  // Tạo Task1 trên Core 0 (đọc cảm biến)
  xTaskCreatePinnedToCore(
    Task1code,   /* Task function */
    "Task1",     /* name of task */
    10000,       /* Stack size of task */
    NULL,        /* parameter of the task */
    1,           /* priority of the task */
    &Task1,      /* Task handle to keep track of created task */
    0);          /* pin task to core 0 */
  
  delay(500);

  // Tạo Task2 trên Core 1 (MQTT communication)
  xTaskCreatePinnedToCore(
    Task2code,   /* Task function */
    "Task2",     /* name of task */
    10000,       /* Stack size of task */
    NULL,        /* parameter of the task */
    1,           /* priority of the task */
    &Task2,      /* Task handle to keep track of created task */
    1);          /* pin task to core 1 */
  
  delay(500);
}

void loop() {
  // Loop chính để trống vì đã sử dụng tasks
  // Có thể thêm watchdog hoặc các task khác ở đây nếu cần
  delay(1000);
}