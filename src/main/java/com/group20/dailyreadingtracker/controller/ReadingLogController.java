package com.group20.dailyreadingtracker.controller;

import com.group20.dailyreadingtracker.dto.ReadingLogDto;
import com.group20.dailyreadingtracker.entity.ReadingLog;
import com.group20.dailyreadingtracker.server.ReadingLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/reading-logs")
@RequiredArgsConstructor
public class ReadingLogController {
    private final ReadingLogService service;

    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody @Valid ReadingLogDto dto, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        ReadingLog log = service.createLog(userId, dto);
        return ResponseEntity.ok(Map.of("id", log.getId(), "message", "Reading log created successfully"));
    }

    private Long getUserIdFromPrincipal(Principal principal) {
        // 解析当前用户ID（假设 Spring Security 处理身份验证）
        return Long.valueOf(principal.getName());
    }
}

