package com.group20.dailyreadingtracker.controller;

import com.group20.dailyreadingtracker.entity.ReadingLog;
import com.group20.dailyreadingtracker.service.AdminService;
import com.group20.dailyreadingtracker.service.ReadingLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // 只有管理员可以访问
public class AdminController {
    private final ReadingLogService service;
    private final AdminService adminService;

    @GetMapping("/logs")
    public ResponseEntity<List<ReadingLog>> getAllUserLogs() {
        List<ReadingLog> logs = service.getAllLogs();
        return ResponseEntity.ok(logs);
    }
    @DeleteMapping("/reading-logs/{logId}")
    public ResponseEntity<?> deleteInappropriateLog(@PathVariable Long logId) {
        service.deleteInappropriateLog(logId);
        return ResponseEntity.ok(Map.of("message", "Inappropriate reading log deleted by admin"));
    }
    @PostMapping("/lock-user/{userId}")
    public ResponseEntity<?> lockUser(@PathVariable Long userId) {
        adminService.lockUser(userId);
        return ResponseEntity.ok(Map.of("message", "User locked successfully"));
    }

    @PostMapping("/unlock-user/{userId}")
    public ResponseEntity<?> unlockUser(@PathVariable Long userId) {
        adminService.unlockUser(userId);
        return ResponseEntity.ok(Map.of("message", "User unlocked successfully"));
    }
}
