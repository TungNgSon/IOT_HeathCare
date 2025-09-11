package com.example.IOT_HealthCare.IOT_HeathCare.repository;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.DeviceAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;

@Repository
public interface DeviceActionRepository extends JpaRepository<DeviceAction, Integer> {
    Page<DeviceAction> findByIdBetween(Integer minId, Integer maxId, Pageable pageable);
    Page<DeviceAction> findByTimeBetween(Date startDate, Date endDate, Pageable pageable);
    Page<DeviceAction> findByDevice(String device, Pageable pageable);
    Page<DeviceAction> findByAction(String action, Pageable pageable);

}