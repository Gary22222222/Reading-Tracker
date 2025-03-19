package com.group20.dailyreadingtracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViolationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long logId;
    private Long userId;
    private String reason;
    private LocalDateTime deletedAt;

    public ViolationLog(ReadingLog log) {
        this.logId = log.getId();
        this.userId = log.getUser().getId();
        this.reason = "Violation of content policy";
        this.deletedAt = LocalDateTime.now();
    }
}
