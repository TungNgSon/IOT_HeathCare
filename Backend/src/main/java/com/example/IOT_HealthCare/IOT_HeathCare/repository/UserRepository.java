package com.example.IOT_HealthCare.IOT_HeathCare.repository;

import com.example.IOT_HealthCare.IOT_HeathCare.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
