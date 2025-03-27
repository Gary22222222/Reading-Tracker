package com.group20.dailyreadingtracker;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.group20.dailyreadingtracker.readinglog.ReadingLog;
import com.group20.dailyreadingtracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/admin";
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // 插入测试用户（确保数据库中有 userId=2）
        User testUser = new User();
        testUser.setId(2L);
        testUser.setUsername("testAdmin");
        testUser.setRole("USER"); // 也可以是 "ADMIN"
        userRepository.save(testUser);
    }


    /**
     * ✅ 测试获取所有用户的阅读日志
     */
    @Test
    void testGetAllUserLogs() throws Exception {
        mockMvc.perform(get(getBaseUrl() + "/logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0))); // 确保返回的是列表
    }

    /**
     * ✅ 测试管理员删除违规日志
     */
    @Test
    void testDeleteInappropriateLog() throws Exception {
        Long logId = 1L;

        mockMvc.perform(delete(getBaseUrl() + "/reading-logs/" + logId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inappropriate reading log deleted by admin"));
    }

    /**
     * ✅ 测试管理员锁定用户
     */
    @Test
    void testLockUser() throws Exception {
        Long userId = 2L;

        mockMvc.perform(post(getBaseUrl() + "/lock-user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User locked successfully"));
    }

    /**
     * ✅ 测试管理员解锁用户
     */
    @Test
    void testUnlockUser() throws Exception {
        Long userId = 2L;

        mockMvc.perform(post(getBaseUrl() + "/unlock-user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User unlocked successfully"));
    }
}
