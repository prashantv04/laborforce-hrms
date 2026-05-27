package com.example.HRMS.demo.worker.repository;

import com.example.HRMS.demo.worker.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findByPhone(String phone);
}