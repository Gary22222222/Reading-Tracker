package com.group20.dailyreadingtracker.violationlog;

import com.group20.dailyreadingtracker.violationlog.ViolationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationLogRepository extends JpaRepository<ViolationLog, Long> {
}

