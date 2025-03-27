package com.group20.dailyreadingtracker;



import com.group20.dailyreadingtracker.Admin.AdminController;
import com.group20.dailyreadingtracker.Admin.AdminService;
import com.group20.dailyreadingtracker.readinglog.ReadingLog;
import com.group20.dailyreadingtracker.readinglog.ReadingLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private ReadingLogService readingLogService;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    /**
     * 测试获取所有用户日志
     */
    @Test
    void testGetAllUserLogs() {
        // 准备模拟数据
        ReadingLog log1 = new ReadingLog();
        log1.setId(1L);
        log1.setTitle("Test Log 1");

        ReadingLog log2 = new ReadingLog();
        log2.setId(2L);
        log2.setTitle("Test Log 2");

        List<ReadingLog> mockLogs = List.of(log1, log2);

        // Mock 行为
        when(readingLogService.getAllLogs()).thenReturn(mockLogs);

        // 调用 API
        ResponseEntity<List<ReadingLog>> response = adminController.getAllUserLogs();

        // 验证返回结果
        assertEquals(2, response.getBody().size());
        assertEquals("Test Log 1", response.getBody().get(0).getTitle());
        assertEquals("Test Log 2", response.getBody().get(1).getTitle());

        // 验证方法调用次数
        verify(readingLogService, times(1)).getAllLogs();
    }

    /**
     * 测试删除违规日志
     */
    @Test
    void testDeleteInappropriateLog() {
        Long logId = 1L;

        // 调用 API
        ResponseEntity<?> response = adminController.deleteInappropriateLog(logId);

        // 验证返回结果
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Inappropriate reading log deleted by admin", ((Map) response.getBody()).get("message"));

        // 验证 deleteInappropriateLog 方法是否被调用
        verify(readingLogService, times(1)).deleteInappropriateLog(logId);
    }

    /**
     * 测试锁定用户
     */
    @Test
    void testLockUser() {
        Long userId = 1L;

        // 调用 API
        ResponseEntity<?> response = adminController.lockUser(userId);

        // 验证返回结果
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User locked successfully", ((Map) response.getBody()).get("message"));

        // 验证 lockUser 方法是否被调用
        verify(adminService, times(1)).lockUser(userId);
    }

    /**
     * 测试解锁用户
     */
    @Test
    void testUnlockUser() {
        Long userId = 1L;

        // 调用 API
        ResponseEntity<?> response = adminController.unlockUser(userId);

        // 验证返回结果
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User unlocked successfully", ((Map) response.getBody()).get("message"));

        // 验证 unlockUser 方法是否被调用
        verify(adminService, times(1)).unlockUser(userId);
    }
}
