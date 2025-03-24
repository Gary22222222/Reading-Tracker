package com.group20.dailyreadingtracker;


import com.group20.dailyreadingtracker.controller.ReadingLogController;
import com.group20.dailyreadingtracker.entity.ReadingLog;
import com.group20.dailyreadingtracker.service.ReadingLogService;
import com.group20.dailyreadingtracker.dto.ReadingLogDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReadingLogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReadingLogService readingLogService;

    @InjectMocks
    private ReadingLogController readingLogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(readingLogController).build();
    }

    @Test
    void testCreateLog() throws Exception {
        ReadingLogDto dto = new ReadingLogDto("Spring Boot", "John Doe", null, 30, "Test");

        when(readingLogService.createLog(1L, dto)).thenReturn(new ReadingLog());

        mockMvc.perform(post("/api/reading-logs")
                        .contentType("application/json")
                        .content("{\"title\":\"Spring Boot\", \"author\":\"John Doe\", \"timeSpent\":30, \"notes\":\"Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Reading log created successfully"));
    }
}
