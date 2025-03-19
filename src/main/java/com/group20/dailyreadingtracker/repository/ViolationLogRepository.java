package com.group20.dailyreadingtracker.repository;

import com.group20.dailyreadingtracker.entity.ViolationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationLogRepository extends JpaRepository<ViolationLog, Long> {
}

