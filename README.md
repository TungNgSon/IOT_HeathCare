# Health Monitoring IoT System  

## 📌 Giới thiệu  
Dự án xây dựng một hệ thống **IoT giám sát sức khỏe** sử dụng **ESP32** kết hợp các cảm biến:  
- **DS18B20**: đo nhiệt độ cơ thể  
- **MAX30102**: đo nhịp tim và SpO₂  
- **LED + điện trở**: mô phỏng điều khiển thiết bị ngoại vi (bật/tắt từ web)  

Dữ liệu cảm biến và trạng thái LED được truyền thông qua **MQTT** đến server, nơi backend (**Spring Boot**) xử lý và lưu trữ. Frontend (**Angular**) cung cấp giao diện web để hiển thị chỉ số sức khỏe và quản lý trạng thái thiết bị.  

---

## ⚙️ Kiến trúc hệ thống  
- **ESP32**: thu thập dữ liệu cảm biến, publish/subcribe MQTT  
- **MQTT Broker**: trung gian giao tiếp (Mosquitto hoặc HiveMQ)  
- **Spring Boot (Backend)**:  
  - Kết nối MQTT  
  - Lưu dữ liệu vào cơ sở dữ liệu (MySQL/PostgreSQL)  
  - Cung cấp REST API cho frontend  
- **Angular (Frontend)**:  
  - Hiển thị chỉ số nhiệt độ, nhịp tim, SpO₂  
  - Trang quản lý lịch sử bật/tắt LED  
  - Giao diện trực quan, real-time  

---

## 🚀 Chức năng chính  
1. **Giám sát sức khỏe**  
   - Hiển thị nhiệt độ cơ thể  
   - Theo dõi nhịp tim, nồng độ SpO₂  
   - Lưu lại dữ liệu theo thời gian  

2. **Điều khiển thiết bị**  
   - Bật/tắt LED từ giao diện web  
   - Quản lý và xem lịch sử hoạt động  

---

## 🛠️ Công nghệ sử dụng  
- **Phần cứng**: ESP32, DS18B20, MAX30102, LED  
- **Giao thức**: MQTT  
- **Backend**: Spring Boot, JPA/Hibernate, MySQL  
- **Frontend**: Angular  
- **Broker**: Eclipse Mosquitto (hoặc HiveMQ)  

---
