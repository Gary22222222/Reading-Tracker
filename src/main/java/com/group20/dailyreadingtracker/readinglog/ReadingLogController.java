package com.group20.dailyreadingtracker.readinglog;

import com.group20.dailyreadingtracker.readinglog.ReadingLogDto;
import com.group20.dailyreadingtracker.readinglog.ReadingLog;
import com.group20.dailyreadingtracker.readinglog.ReadingLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reading-logs")
@RequiredArgsConstructor
public class ReadingLogController {
    private final ReadingLogService service;
    // 🔹 获取当前用户的所有阅读日志
    @GetMapping("/api/reading-logs")
    public ResponseEntity<List<ReadingLog>> getAllLogs(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<ReadingLog> logs = service.getAllLogsByUser(userId);
        return ResponseEntity.ok(logs);
    }

    // 🔹 获取某个日志的详情
    @GetMapping("/api/reading-logs/{logId}")
    public ResponseEntity<ReadingLog> getLogById(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        ReadingLog log = service.getLogById(userId, id);
        return ResponseEntity.ok(log);
    }
    @PutMapping("/api/reading-logs/{logId}")
    public ResponseEntity<?> updateLog(@PathVariable Long id, @RequestBody @Valid ReadingLogDto dto, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        ReadingLog updatedLog = service.updateLog(userId, id, dto);
        return ResponseEntity.ok(Map.of("id", updatedLog.getId(), "message", "Reading log updated successfully"));
    }



    @PostMapping("/api/reading-logs")
    public ResponseEntity<?> createLog(@RequestBody @Valid ReadingLogDto dto, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        ReadingLog log = service.createLog(userId, dto);
        return ResponseEntity.ok(Map.of("id", log.getId(), "message", "Reading log created successfully"));
    }
    @DeleteMapping("/api/reading-logs/{logId}")
    public ResponseEntity<?> deleteLog(@PathVariable Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        service.deleteLog(userId, id);
        return ResponseEntity.ok(Map.of("message", "Reading log deleted successfully"));
    }


    private Long getUserIdFromPrincipal(Principal principal) {
        // 解析当前用户ID（假设 Spring Security 处理身份验证）
        return Long.valueOf(principal.getName());
    }
}

