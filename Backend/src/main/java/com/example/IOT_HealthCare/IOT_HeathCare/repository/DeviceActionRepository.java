package com.example.IOT_HealthCare.IOT_HeathCare.repository;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DataSensor;
import com.example.IOT_HealthCare.IOT_HeathCare.entities.DeviceAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;

@Repository
public interface DeviceActionRepository extends JpaRepository<DeviceAction, Integer> {
    Page<DeviceAction> findByIdBetween(Integer minId, Integer maxId, Pageable pageable);
    Page<DeviceAction> findByTimeBetween(Date startDate, Date endDate, Pageable pageable);
    Page<DeviceAction> findByDevice(String device, Pageable pageable);
    Page<DeviceAction> findByAction(String action, Pageable pageable);
    @Query(value = "SELECT * FROM device_action WHERE " +
            "CASE WHEN :patternType = 'year' THEN YEAR(time) = :pattern " +
            "WHEN :patternType = 'month' THEN DATE_FORMAT(time, '%Y-%m') = :pattern " +
            "WHEN :patternType = 'day' THEN DATE_FORMAT(time, '%Y-%m-%d') = :pattern " +
            "WHEN :patternType = 'hour' THEN DATE_FORMAT(time, '%Y-%m-%d %H') = :pattern " +
            "WHEN :patternType = 'minute' THEN DATE_FORMAT(time, '%Y-%m-%d %H:%i') = :pattern " +
            "WHEN :patternType = 'second' THEN DATE_FORMAT(time, '%Y-%m-%d %H:%i:%s') = :pattern " +
            "END = 1",
            nativeQuery = true)
    Page<DeviceAction> findByTimePattern(@Param("pattern") String pattern,
                                       @Param("patternType") String patternType,
                                       Pageable pageable);

    // Exact value search methods
    @Query(value = "SELECT * FROM device_action WHERE " +
            "CASE WHEN :column = 'id' THEN CAST(id AS DOUBLE) " +
            "WHEN :column = 'device' THEN device " +
            "WHEN :column = 'action' THEN action " +
            "END = :value",
            nativeQuery = true)
    Page<DeviceAction> findByColumnAndExactValue(@Param("column") String column,
                                                @Param("value") String value,
                                                Pageable pageable);

    // Exact time search
    @Query("SELECT d FROM DeviceAction d WHERE d.time = :exactTime")
    Page<DeviceAction> findByExactTime(@Param("exactTime") Date exactTime, Pageable pageable);

    @Query(value = "SELECT * FROM device_action " +
            "WHERE device = :device " +
            "ORDER BY time DESC " +
            "LIMIT 1",
            nativeQuery = true)
    DeviceAction findLatestActionByDevice(@Param("device") String device);

    // Multi-filter search method với time pattern support (sử dụng native query)
    @Query(value = "SELECT * FROM device_action WHERE " +
            "(:device IS NULL OR device = :device) AND " +
            "(:action IS NULL OR action = :action) AND " +
            "(:timePattern IS NULL OR " +
            "CASE WHEN :patternType = 'year' THEN YEAR(time) = :timePattern " +
            "WHEN :patternType = 'month' THEN DATE_FORMAT(time, '%Y-%m') = :timePattern " +
            "WHEN :patternType = 'day' THEN DATE_FORMAT(time, '%Y-%m-%d') = :timePattern " +
            "WHEN :patternType = 'hour' THEN DATE_FORMAT(time, '%Y-%m-%d %H') = :timePattern " +
            "WHEN :patternType = 'minute' THEN DATE_FORMAT(time, '%Y-%m-%d %H:%i') = :timePattern " +
            "WHEN :patternType = 'second' THEN DATE_FORMAT(time, '%Y-%m-%d %H:%i:%s') = :timePattern " +
            "WHEN :patternType = 'exact' THEN time = :exactTime " +
            "END = 1)",
            nativeQuery = true)
    Page<DeviceAction> findByMultipleFiltersWithTimePattern(@Param("device") String device,
                                                           @Param("action") String action,
                                                           @Param("timePattern") String timePattern,
                                                           @Param("patternType") String patternType,
                                                           @Param("exactTime") Date exactTime,
                                                           Pageable pageable);
}