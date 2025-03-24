package com.group20.dailyreadingtracker;



import com.group20.dailyreadingtracker.dto.ReadingLogDto;
import com.group20.dailyreadingtracker.entity.ReadingLog;
import com.group20.dailyreadingtracker.entity.User;
import com.group20.dailyreadingtracker.repository.ReadingLogRepository;
import com.group20.dailyreadingtracker.service.ReadingLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReadingLogServiceTest {

    @Mock
    private ReadingLogRepository readingLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReadingLogService readingLogService;

    private User user;
    private ReadingLog log;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // 初始化 Mock 对象
        user = new User();
        user.setId(1L);
        user.setEmail("test@domain.com");

        log = new ReadingLog();
        log.setId(1L);
        log.setTitle("Spring Boot");
        log.setAuthor("John Doe");
        log.setUser(user);
    }

    @Test
    void testCreateLog() {
        // 预设 UserRepository 的行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 预设 ReadingLogRepository 的行为
        when(readingLogRepository.save(any(ReadingLog.class))).thenReturn(log);

        // 调用服务层方法
        ReadingLog createdLog = readingLogService.createLog(1L, new ReadingLogDto("Spring Boot", "John Doe", null, 30, "Test"));

        // 断言返回的日志是我们预期的
        assertNotNull(createdLog);
        assertEquals("Spring Boot", createdLog.getTitle());
        verify(readingLogRepository, times(1)).save(any(ReadingLog.class));  // 确保保存方法被调用
    }

    @Test
    void testDeleteUserLog_notFound() {
        // 预设当日志不存在时的行为
        when(readingLogRepository.findById(2L)).thenReturn(Optional.empty());

        // 测试删除时抛出 RuntimeException 异常
        assertThrows(RuntimeException.class, () -> {
            readingLogService.deleteLog(1L, 2L);
        });

        verify(readingLogRepository, times(0)).delete(any(ReadingLog.class));  // 确保删除没有被调用
    }

    @Test
    void testDeleteUserLog_success() {
        // 预设读取日志的行为
        when(readingLogRepository.findById(1L)).thenReturn(Optional.of(log));

        // 调用删除日志
        readingLogService.deleteLog(1L, 1L);

        // 验证删除操作
        verify(readingLogRepository, times(1)).delete(log);
    }
}

