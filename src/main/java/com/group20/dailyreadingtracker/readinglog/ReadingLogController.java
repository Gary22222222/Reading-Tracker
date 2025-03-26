package com.group20.dailyreadingtracker.readinglog;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reading-logs")
@RequiredArgsConstructor
public class ReadingLogController {
    private final ReadingLogService service;

    // 🔹 获取当前用户的所有阅读日志
    @GetMapping
    public ResponseEntity<List<ReadingLog>> getAllLogs(Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        List<ReadingLog> logs = service.getAllLogsByUser(userId);
        return ResponseEntity.ok(logs);
    }

    // 🔹 获取某个日志的详情
    @GetMapping("/{logId}")
    public ResponseEntity<?> getLogById(@PathVariable("logId") Long id, Principal principal) {
        if (id <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid log ID format"));
        }
        Long userId = getUserIdFromPrincipal(principal);
        try {
            ReadingLog log = service.getLogById(userId, id);
            return ResponseEntity.ok(log);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Reading log not found"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }

    // 🔹 更新阅读日志
    @PutMapping("/{logId}")
    public ResponseEntity<?> updateLog(@PathVariable("logId") Long id, @RequestBody @Valid ReadingLogDto dto, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        try {
            ReadingLog updatedLog = service.updateLog(userId, id, dto);
            return ResponseEntity.ok(Map.of("id", updatedLog.getId(), "message", "Reading log updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Reading log not found"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }

    // 🔹 创建阅读日志
    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody @Valid ReadingLogDto dto, Principal principal) {
        if (dto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request body cannot be null"));
        }
        Long userId = getUserIdFromPrincipal(principal);
        try {
            ReadingLog log = service.createLog(userId, dto);
            return ResponseEntity.ok(Map.of("id", log.getId(), "message", "Reading log created successfully"));
        } catch (IllegalArgumentException e) {
            if ("Payload too large".equals(e.getMessage())) {
                return ResponseEntity.status(413).body(Map.of("error", "Payload too large"));
            }
            throw e; // 其他异常继续抛出
        }
    }



    // 🔹 删除阅读日志
    @DeleteMapping("/{logId}")
    public ResponseEntity<?> deleteLog(@PathVariable("logId") Long id, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        try {
            service.deleteLog(userId, id);
            return ResponseEntity.ok(Map.of("message", "Reading log deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Reading log not found"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        try {
            return Long.valueOf(principal.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }
    }
    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException ex) {
            return ResponseEntity.status(413).body(Map.of("error", "Payload too large"));
        }
    }


}


