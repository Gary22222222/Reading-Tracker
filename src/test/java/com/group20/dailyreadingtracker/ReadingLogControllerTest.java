package com.group20.dailyreadingtracker;

import com.group20.dailyreadingtracker.readinglog.ReadingLog;
import com.group20.dailyreadingtracker.readinglog.ReadingLogController;
import com.group20.dailyreadingtracker.readinglog.ReadingLogDto;
import com.group20.dailyreadingtracker.readinglog.ReadingLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingLogControllerTest {

    @Mock
    private ReadingLogService readingLogService;

    @InjectMocks
    private ReadingLogController readingLogController;

    private Principal mockPrincipal;
    private static final Long USER_ID = 1L;
    private static final Long LOG_ID = 100L;

    @BeforeEach
    void setUp() {
        mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(String.valueOf(USER_ID));
    }

    @Test
    void shouldReturnAllLogsForUser() {
        List<ReadingLog> mockLogs = List.of(new ReadingLog(), new ReadingLog());
        when(readingLogService.getAllLogsByUser(USER_ID)).thenReturn(mockLogs);

        ResponseEntity<List<ReadingLog>> response = readingLogController.getAllLogs(mockPrincipal);

        assertThat(response.getBody()).isEqualTo(mockLogs);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(readingLogService).getAllLogsByUser(USER_ID);
    }

    @Test
    void shouldReturnSpecificLogForUser() {
        ReadingLog mockLog = new ReadingLog();
        when(readingLogService.getLogById(USER_ID, LOG_ID)).thenReturn(mockLog);

        ResponseEntity<ReadingLog> response = readingLogController.getLogById(LOG_ID, mockPrincipal);

        assertThat(response.getBody()).isEqualTo(mockLog);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(readingLogService).getLogById(USER_ID, LOG_ID);
    }

    @Test
    void shouldCreateNewReadingLog() {
        ReadingLogDto dto = new ReadingLogDto();
        ReadingLog mockLog = new ReadingLog();
        mockLog.setId(LOG_ID);
        when(readingLogService.createLog(USER_ID, dto)).thenReturn(mockLog);

        ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) readingLogController.createLog(dto, mockPrincipal);

        assertThat(response.getBody()).containsEntry("id", LOG_ID)
                .containsEntry("message", "Reading log created successfully");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(readingLogService).createLog(USER_ID, dto);
    }

    @Test
    void shouldUpdateReadingLog() {
        ReadingLogDto dto = new ReadingLogDto();
        ReadingLog updatedLog = new ReadingLog();
        updatedLog.setId(LOG_ID);
        when(readingLogService.updateLog(USER_ID, LOG_ID, dto)).thenReturn(updatedLog);

        ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) readingLogController.updateLog(LOG_ID, dto, mockPrincipal);

        assertThat(response.getBody()).containsEntry("id", LOG_ID)
                .containsEntry("message", "Reading log updated successfully");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(readingLogService).updateLog(USER_ID, LOG_ID, dto);
    }

    @Test
    void shouldDeleteReadingLog() {
        doNothing().when(readingLogService).deleteLog(USER_ID, LOG_ID);

        ResponseEntity<Map<String, String>> response = (ResponseEntity<Map<String, String>>) readingLogController.deleteLog(LOG_ID, mockPrincipal);

        assertThat(response.getBody()).containsEntry("message", "Reading log deleted successfully");
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(readingLogService).deleteLog(USER_ID, LOG_ID);
    }
}

