package com.group20.dailyreadingtracker.repository;

import com.group20.dailyreadingtracker.entity.ReadingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ReadingLogRepository extends JpaRepository<ReadingLog, Long> {
    List<ReadingLog> findByUserId(Long userId);
}
