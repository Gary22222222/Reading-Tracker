package com.group20.dailyreadingtracker;
import com.group20.dailyreadingtracker.violationlog.ViolationLogRepository;
import org.springframework.web.server.ResponseStatusException;

import com.group20.dailyreadingtracker.readinglog.ReadingLogDto;
import com.group20.dailyreadingtracker.readinglog.ReadingLog;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.readinglog.ReadingLogRepository;
import com.group20.dailyreadingtracker.readinglog.ReadingLogService;
import com.group20.dailyreadingtracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;


import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReadingLogServiceTest {

    @Mock
    private ReadingLogRepository readingLogRepository;


    @Mock
    private ViolationLogRepository violationLogRepository; // Mock ViolationLogRepository

    @InjectMocks
    private ReadingLogService readingLogService; // Inject mocks into ReadingLogService

    @Mock
    private UserRepository userRepository;



    private User user;
    private ReadingLog log;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 初始化 Mock 对象
        user = new User();
        user.setId(1L);
        user.setEmail("test@domain.com");

        log = new ReadingLog();
        log.setId(1L);
        log.setTitle("Spring Boot");
        log.setAuthor("John Doe");
        log.setUser(user);
    }

    // 测试创建日志
    @Test
    void testCreateLog() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(readingLogRepository.save(any(ReadingLog.class))).thenReturn(log);

        ReadingLog createdLog = readingLogService.createLog(1L, new ReadingLogDto("Spring Boot", "John Doe", null, 30, "Test"));

        assertNotNull(createdLog);
        assertEquals("Spring Boot", createdLog.getTitle());
        verify(readingLogRepository, times(1)).save(any(ReadingLog.class));
    }

    // 测试删除日志 - 日志不存在
    @Test
    void testDeleteUserLog_notFound() {
        when(readingLogRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            readingLogService.deleteLog(1L, 2L);
        });

        verify(readingLogRepository, times(0)).delete(any(ReadingLog.class));
    }

    // 测试删除日志 - 成功
    @Test
    void testDeleteUserLog_success() {
        when(readingLogRepository.findById(1L)).thenReturn(Optional.of(log));

        readingLogService.deleteLog(1L, 1L);

        verify(readingLogRepository, times(1)).delete(log);
    }

    // 测试更新日志 - 成功
    @Test
    void testUpdateLog_success() {
        // 预设日志存在且属于用户
        when(readingLogRepository.findById(1L)).thenReturn(Optional.of(log));

        // 创建一个更新后的日志对象
        ReadingLog updatedLog = new ReadingLog();
        updatedLog.setId(1L);
        updatedLog.setTitle("Updated Title");
        updatedLog.setAuthor("Updated Author");
        updatedLog.setUser(user);

        // 预设保存行为
        when(readingLogRepository.save(any(ReadingLog.class))).thenReturn(updatedLog);

        // 调用更新日志
        ReadingLogDto dto = new ReadingLogDto("Updated Title", "Updated Author", null, 60, "Updated Notes");
        ReadingLog resultLog = readingLogService.updateLog(1L, 1L, dto);

        // 验证更新后的日志
        assertNotNull(resultLog);
        assertEquals("Updated Title", resultLog.getTitle());
        assertEquals("Updated Author", resultLog.getAuthor());
        verify(readingLogRepository, times(1)).save(any(ReadingLog.class));
    }

    // 测试更新日志 - 日志不存在
    @Test
    void testUpdateLog_notFound() {
        when(readingLogRepository.findById(1L)).thenReturn(Optional.empty());

        ReadingLogDto dto = new ReadingLogDto("Updated Title", "Updated Author", null, 60, "Updated Notes");
        assertThrows(RuntimeException.class, () -> {
            readingLogService.updateLog(1L, 1L, dto);
        });

        verify(readingLogRepository, times(0)).save(any(ReadingLog.class));
    }

    // 测试更新日志 - 用户无权限
    @Test
    void testUpdateLog_unauthorized() {
        // 预设日志存在但不属于用户
        User anotherUser = new User();
        anotherUser.setId(2L);
        log.setUser(anotherUser);

        when(readingLogRepository.findById(1L)).thenReturn(Optional.of(log));

        // 测试更新时抛出 ResponseStatusException
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            ReadingLogDto dto = new ReadingLogDto("Updated Title", "Updated Author", null, 60, "Updated Notes");
            readingLogService.updateLog(1L, 1L, dto);
        });

        // 验证异常的状态码和消息
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Reading log not found or unauthorized", exception.getReason());

        verify(readingLogRepository, times(1)).findById(1L);
        verify(readingLogRepository, times(0)).save(any(ReadingLog.class));
    }

    // 测试获取单个日志 - 成功
    @Test
    void testGetLogById_success() {
        when(readingLogRepository.findById(1L)).thenReturn(Optional.of(log));

        ReadingLog foundLog = readingLogService.getLogById(1L, 1L);

        assertNotNull(foundLog);
        assertEquals("Spring Boot", foundLog.getTitle());
        verify(readingLogRepository, times(1)).findById(1L);
    }

    // 测试获取单个日志 - 日志不存在
    @Test
    void testGetLogById_notFound() {
        when(readingLogRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            readingLogService.getLogById(1L, 1L);
        });

        verify(readingLogRepository, times(1)).findById(1L);
    }

    // 测试获取单个日志 - 用户无权限
    @Test
    void testGetLogById_unauthorized() {
        // 预设日志存在但不属于用户
        User anotherUser = new User();
        anotherUser.setId(2L);
        log.setUser(anotherUser);

        when(readingLogRepository.findById(1L)).thenReturn(Optional.of(log));

        // 测试获取时抛出 ResponseStatusException
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            readingLogService.getLogById(1L, 1L);
        });

        // 验证异常的状态码和消息
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()); // 验证状态码
        assertEquals("Reading log not found or unauthorized", exception.getReason()); // 验证异常原因

        verify(readingLogRepository, times(1)).findById(1L);
    }



    // 测试获取所有日志 - 成功
    @Test
    void testGetAllLogsByUser_success() {
        when(readingLogRepository.findByUserId(1L)).thenReturn(List.of(log));

        List<ReadingLog> logs = readingLogService.getAllLogsByUser(1L);

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals("Spring Boot", logs.get(0).getTitle());
        verify(readingLogRepository, times(1)).findByUserId(1L);
    }

    // 测试获取所有日志 - 用户没有日志
    @Test
    void testGetAllLogsByUser_empty() {
        when(readingLogRepository.findByUserId(1L)).thenReturn(List.of());

        List<ReadingLog> logs = readingLogService.getAllLogsByUser(1L);

        assertNotNull(logs);
        assertTrue(logs.isEmpty());
        verify(readingLogRepository, times(1)).findByUserId(1L);
    }

    // 测试删除违规日志 - 成功
    @Test
    void testDeleteInappropriateLog_success() {
        when(readingLogRepository.findById(1L)).thenReturn(Optional.of(log));

        readingLogService.deleteInappropriateLog(1L);

        verify(readingLogRepository, times(1)).delete(log);
    }

    // 测试删除违规日志 - 日志不存在
    @Test
    void testDeleteInappropriateLog_notFound() {
        when(readingLogRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            readingLogService.deleteInappropriateLog(1L);
        });

        verify(readingLogRepository, times(0)).delete(any(ReadingLog.class));
    }

    // 测试边界条件 - 创建日志时 DTO 为 null
    @Test
    void testCreateLog_nullDto() {
        assertThrows(RuntimeException.class, () -> {
            readingLogService.createLog(1L, null);
        });

        verify(readingLogRepository, times(0)).save(any(ReadingLog.class));
    }

    // 测试边界条件 - 获取日志时传入负数 ID
    @Test
    void testGetLogById_invalidId() {
        // 模拟 repository 返回空值
        when(readingLogRepository.findById(-1L)).thenReturn(Optional.empty());

        // 调用服务方法并断言抛出异常
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            readingLogService.getLogById(-1L, 1L);
        });

        // 验证异常的状态码和消息
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Reading log not found or unauthorized", exception.getReason());

        // 验证 repository 被调用了一次
        verify(readingLogRepository, times(1)).findById(-1L);
    }

}
