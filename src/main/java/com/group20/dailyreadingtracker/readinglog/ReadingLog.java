package com.group20.dailyreadingtracker.readinglog;

import com.group20.dailyreadingtracker.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reading_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingLog {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 关联用户

    private String title;   // 书籍或文章标题
    private String author;  // 作者
    private LocalDate date; // 阅读日期
    private int timeSpent;  // 阅读时间（单位：分钟）
    private String notes;   // 个人笔记

    @CreatedDate
    private LocalDateTime createdAt;

}

