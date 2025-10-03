package com.example.IOT_HealthCare.IOT_HeathCare.service.impl;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DataSensor;
import com.example.IOT_HealthCare.IOT_HeathCare.handlers.SensorWebSocketHandler;
import com.example.IOT_HealthCare.IOT_HeathCare.repository.DataSensorRepository;
import com.example.IOT_HealthCare.IOT_HeathCare.service.DataSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class DataSensorServiceImpl implements DataSensorService {
    private final SensorWebSocketHandler webSocketHandler;
    private final DataSensorRepository dataSensorRepository;

    // Lưu giá trị gần nhất của từng sensor
    private double lastHeartRate = 0;
    private double lastSpO2 = 0;
    private double lastTemperature = 0.0;
    private boolean heartRateValid = false;
    private boolean spO2Valid = false;
    private boolean temperatureValid = false;

    @Autowired
    public DataSensorServiceImpl(SensorWebSocketHandler webSocketHandler,
                                 DataSensorRepository dataSensorRepository) {
        this.webSocketHandler = webSocketHandler;
        this.dataSensorRepository = dataSensorRepository;
    }

    // Implement Service methods
    @Override
    public void saveTemperature(double temperature) {
        this.lastTemperature = temperature;
        this.temperatureValid = true;
        System.out.println("💾 Lưu nhiệt độ: " + temperature + "°C");
    }

    @Override
    public void handleSensorMessage(String payload) {
        try {
            parseSensorData(payload);
        } catch (Exception e) {
            System.err.println("❌ Lỗi parse dữ liệu sensor: " + e.getMessage());
            webSocketHandler.sendSensorError("parser", "Lỗi parse dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Thêm methods cho Heart Rate và SpO2
    public void saveHeartRate(double heartRate) {
        this.lastHeartRate = heartRate;
        this.heartRateValid = true;
        System.out.println("💾 Lưu nhịp tim: " + heartRate + " BPM");
    }

    public void saveSpO2(double spO2) {
        this.lastSpO2 = spO2;
        this.spO2Valid = true;
        System.out.println("💾 Lưu SpO2: " + spO2 + "%");
    }

    /**
     * Lưu tất cả dữ liệu sensor vào database và gửi qua WebSocket
     */
    public void saveSensorDataToDatabase(double heartRate, boolean hrValid, double spO2, boolean spo2Valid,
                                         double temperature, boolean tempValid) {
        try {
            // Chỉ lưu khi có ít nhất 1 giá trị hợp lệ
            if (!hrValid && !spo2Valid && !tempValid) {
                System.out.println("⚠️ Không có dữ liệu hợp lệ để lưu vào database");
                return;
            }

            // Tạo entity mới
            DataSensor dataSensor = new DataSensor();
            dataSensor.setTime(new Date());

            // Lưu giá trị (nếu không hợp lệ thì lưu giá trị mặc định hoặc giá trị cuối)
            dataSensor.setHeartRate(hrValid ? heartRate : 0.0);
            dataSensor.setSPO2(spo2Valid ? spO2 : 0.0);
            dataSensor.setBodyTemperature(tempValid ? temperature : 0.0);

            // Lưu vào database
            if (hrValid && spo2Valid && tempValid) {
                dataSensorRepository.save(dataSensor);
                System.out.println("✅ Đã lưu dữ liệu vào database với ID: " + dataSensor.getId());
            }

            // Cập nhật giá trị local
            if (hrValid) saveHeartRate(heartRate);
            if (spo2Valid) saveSpO2(spO2);
            if (tempValid) saveTemperature(temperature);

            // Gửi tất cả data qua WebSocket với thông tin từ database
            System.out.println("Ban len voi co " + hrValid + " " + spo2Valid + " ");
            webSocketHandler.sendAllSensorData(
                    dataSensor.getId(),
                    dataSensor.getHeartRate(), hrValid,
                    dataSensor.getSPO2(), spo2Valid,
                    dataSensor.getBodyTemperature(), tempValid,
                    dataSensor.getTime()
            );

            System.out.println("📤 Đã gửi dữ liệu qua WebSocket cho Frontend");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu dữ liệu sensor: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Parse dữ liệu từ ESP32: "BPM: 72, SpO2: 98, Temp: 36.50"
     * Hoặc với giá trị N/A: "BPM: N/A, SpO2: 98, Temp: N/A"
     */
    private void parseSensorData(String payload) {
        try {
            // Split theo dấu phẩy
            String[] parts = payload.split(",");

            double heartRate = 0.0;
            double spO2 = 0.0;
            double temperature = 0.0;
            boolean hrValid = false;
            boolean spo2Valid = false;
            boolean tempValid = false;

            for (String part : parts) {
                part = part.trim();

                // Parse Heart Rate: "BPM: 72" hoặc "BPM: N/A"
                if (part.startsWith("BPM:")) {
                    String bpmStr = part.substring(4).trim();
                    if (!bpmStr.equals("N/A")) {
                        heartRate = Double.parseDouble(bpmStr);
                        hrValid = true;
                    }
                }

                // Parse SpO2: "SpO2: 98" hoặc "SpO2: N/A"
                else if (part.startsWith("SpO2:")) {
                    String spo2Str = part.substring(5).trim();
                    if (!spo2Str.equals("N/A")) {
                        spO2 = Double.parseDouble(spo2Str);
                        spo2Valid = true;
                    }
                }

                // Parse Temperature: "Temp: 36.50" hoặc "Temp: N/A"
                else if (part.startsWith("Temp:")) {
                    String tempStr = part.substring(5).trim();
                    if (!tempStr.equals("N/A")) {
                        temperature = Double.parseDouble(tempStr);
                        tempValid = true;
                    }
                }
            }

            // Lưu dữ liệu vào database và gửi WebSocket
            saveSensorDataToDatabase(heartRate, hrValid, spO2, spo2Valid, temperature, tempValid);

            // Log kết quả
            System.out.println("🔍 Parsed -> HR: " + (hrValid ? heartRate + " BPM" : "N/A") +
                    ", SpO2: " + (spo2Valid ? spO2 + "%" : "N/A") +
                    ", Temp: " + (tempValid ? temperature + "°C" : "N/A"));

        } catch (NumberFormatException e) {
            System.err.println("❌ Lỗi parse số: " + e.getMessage());
            webSocketHandler.sendSensorError("parser", "Lỗi định dạng số: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định khi parse: " + e.getMessage());
            webSocketHandler.sendSensorError("parser", "Lỗi không xác định: " + e.getMessage());
        }
    }

    // Getter methods để frontend có thể lấy dữ liệu gần nhất
    public double getLastHeartRate() {
        return heartRateValid ? lastHeartRate : 0.0;
    }

    public double getLastSpO2() {
        return spO2Valid ? lastSpO2 : 0.0;
    }

    public double getLastTemperature() {
        return temperatureValid ? lastTemperature : 0.0;
    }

    public boolean isHeartRateValid() {
        return heartRateValid;
    }

    public boolean isSpO2Valid() {
        return spO2Valid;
    }

    public boolean isTemperatureValid() {
        return temperatureValid;
    }

    // Method để lấy dữ liệu mới nhất từ database
    public DataSensor getLatestSensorData() {
        try {
            return dataSensorRepository.findLatestRecord();
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy dữ liệu mới nhất: " + e.getMessage());
            return null;
        }
    }

    // Method để lấy dữ liệu hôm nay
    public java.util.List<DataSensor> getTodayData() {
        try {
            return dataSensorRepository.findTodayRecords();
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy dữ liệu hôm nay: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // Method để lấy N records mới nhất
    public java.util.List<DataSensor> getRecentData(int limit) {
        try {
            return dataSensorRepository.findTopNRecords(limit);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy dữ liệu gần đây: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // lay cac ban ghi trong 24h gan nhat
    public List<DataSensor> findLast24HoursData() {
        return dataSensorRepository.find24HRecordsAgo();
    }

    // lay tat ca ban ghi de phan trang
    @Override
    public Page<DataSensor> findAll(Pageable pageable) {
        return dataSensorRepository.findAll(pageable);
    }

    @Override
    public Page<DataSensor> findByNumericColumnAndValueRange(String column, double minValue, double maxValue, Pageable pageable) {
        try {
            // Validate column name
            if (!column.equals("id") && !column.equals("heartRate") && !column.equals("SPO2") && !column.equals("bodyTemperature")) {
                throw new IllegalArgumentException("Cột không hợp lệ: " + column);
            }
            return dataSensorRepository.findByNumericColumnAndValueRange(column, minValue, maxValue, pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo cột số và giá trị: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<DataSensor> findByTimeRange(Date startTime, Date endTime, Pageable pageable) {
        try {
            return dataSensorRepository.findByTimeRange(startTime, endTime, pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo khoảng thời gian: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<DataSensor> findByNumericColumnAndExactValue(String column, double value, Pageable pageable) {
        try {
            // Validate column name
            if (!column.equals("id") && !column.equals("heartRate") && !column.equals("SPO2") && !column.equals("bodyTemperature")) {
                throw new IllegalArgumentException("Cột không hợp lệ: " + column);
            }
            return dataSensorRepository.findByNumericColumnAndExactValue(column, value, pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo giá trị cụ thể: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

//    @Override
//    public Page<DataSensor> findByExactTime(Date exactTime, Pageable pageable) {
//        try {
//            return dataSensorRepository.findByExactTime(exactTime, pageable);
//        } catch (Exception e) {
//            System.err.println("❌ Lỗi khi tìm kiếm theo thời gian chính xác: " + e.getMessage());
//            return Page.empty(pageable);
//        }
//    }

    @Override
    public Page<DataSensor> findByTimePattern(String timePattern, String patternType, Pageable pageable) {
        try {
            return dataSensorRepository.findByTimePattern(timePattern, patternType, pageable);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm kiếm theo pattern thời gian: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    public Page<DataSensor> searchByTimeValue(String timeValue, Pageable pageable) throws ParseException {

        String[] patternInfo = parseTimeInput(timeValue);
        if (patternInfo == null) {
            throw new IllegalArgumentException("Định dạng thời gian không hợp lệ: " + timeValue);
        }

        return findByTimePattern(patternInfo[0], patternInfo[1], pageable);
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
            // THÊM DÒNG NÀY - để dùng pattern search thay vì exact search
            return new String[]{timeInput, "second"};
        }

        return null;
    }
}