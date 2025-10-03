package com.example.IOT_HealthCare.IOT_HeathCare.repository;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DataSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DataSensorRepository extends JpaRepository<DataSensor, Integer> {

    // Lấy dữ liệu mới nhất
    @Query("SELECT d FROM DataSensor d ORDER BY d.time DESC")
    List<DataSensor> findAllByOrderByTimeDesc();

    // Lấy 1 record mới nhất
    @Query("SELECT d FROM DataSensor d ORDER BY d.time DESC LIMIT 1")
    DataSensor findLatestRecord();

    // Lấy dữ liệu theo khoảng thời gian
    @Query("SELECT d FROM DataSensor d WHERE d.time BETWEEN :startDate AND :endDate ORDER BY d.time DESC")
    List<DataSensor> findByTimeBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Lấy N records mới nhất
    @Query("SELECT d FROM DataSensor d ORDER BY d.time DESC LIMIT :limit")
    List<DataSensor> findTopNRecords(@Param("limit") int limit);

    // Đếm số records trong ngày hôm nay
    @Query("SELECT COUNT(d) FROM DataSensor d WHERE DATE(d.time) = CURRENT_DATE")
    long countTodayRecords();

    // Lấy dữ liệu hôm nay
    @Query("SELECT d FROM DataSensor d WHERE DATE(d.time) = CURRENT_DATE ORDER BY d.time DESC")
    List<DataSensor> findTodayRecords();

    // lấy các bản ghi trong vòng 24h vừa qua
    @Query(value = "SELECT * FROM data_sensor WHERE time >= DATE_SUB(NOW(), INTERVAL 24 HOUR)", nativeQuery = true)
    List<DataSensor> find24HRecordsAgo();

    // Tìm kiếm động theo cột số và khoảng giá trị với phân trang
    @Query("SELECT d FROM DataSensor d WHERE " +
            "(:column = 'id' AND d.id BETWEEN :minValue AND :maxValue) OR " +
            "(:column = 'heartRate' AND d.heartRate BETWEEN :minValue AND :maxValue) OR " +
            "(:column = 'SPO2' AND d.SPO2 BETWEEN :minValue AND :maxValue) OR " +
            "(:column = 'bodyTemperature' AND d.bodyTemperature BETWEEN :minValue AND :maxValue)")
    Page<DataSensor> findByNumericColumnAndValueRange(@Param("column") String column,
                                                      @Param("minValue") double minValue,
                                                      @Param("maxValue") double maxValue,
                                                      Pageable pageable);

    // Tìm kiếm theo cột time và khoảng thời gian với phân trang
    @Query("SELECT d FROM DataSensor d WHERE d.time BETWEEN :startTime AND :endTime")
    Page<DataSensor> findByTimeRange(@Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime,
                                     Pageable pageable);
    @Query(value = "SELECT * FROM data_sensor WHERE " +
            "CASE WHEN :column = 'id' THEN CAST(id AS DOUBLE) " +
            "WHEN :column = 'heartRate' THEN heart_rate " +
            "WHEN :column = 'SPO2' THEN spo2 " +
            "WHEN :column = 'bodyTemperature' THEN body_temperature " +
            "END = :value",
            nativeQuery = true)
    Page<DataSensor> findByNumericColumnAndExactValue(@Param("column") String column,
                                                      @Param("value") double value,
                                                      Pageable pageable);

    // Exact time search
//    @Query("SELECT d FROM DataSensor d WHERE d.time = :exactTime")
//    Page<DataSensor> findByExactTime(@Param("exactTime") Date exactTime, Pageable pageable);

    // Time pattern search - for year/month/day/hour/minute
    @Query(value = "SELECT * FROM data_sensor WHERE " +
            "CASE WHEN :patternType = 'year' THEN YEAR(time) = :pattern " +
            "WHEN :patternType = 'month' THEN DATE_FORMAT(time, '%Y-%m') = :pattern " +
            "WHEN :patternType = 'day' THEN DATE_FORMAT(time, '%Y-%m-%d') = :pattern " +
            "WHEN :patternType = 'hour' THEN DATE_FORMAT(time, '%Y-%m-%d %H') = :pattern " +
            "WHEN :patternType = 'minute' THEN DATE_FORMAT(time, '%Y-%m-%d %H:%i') = :pattern " +
            "WHEN :patternType = 'second' THEN DATE_FORMAT(time, '%Y-%m-%d %H:%i:%s') = :pattern " + // THÊM DÒNG NÀY
            "END = 1",
            nativeQuery = true)
    Page<DataSensor> findByTimePattern(@Param("pattern") String pattern,
                                       @Param("patternType") String patternType,
                                       Pageable pageable);
}