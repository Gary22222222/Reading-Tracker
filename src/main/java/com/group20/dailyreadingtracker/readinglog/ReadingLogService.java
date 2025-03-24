package com.group20.dailyreadingtracker.readinglog;



import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.violationlog.ViolationLog;
import com.group20.dailyreadingtracker.user.UserRepository;
import com.group20.dailyreadingtracker.violationlog.ViolationLogRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadingLogService {
    private final ReadingLogRepository readingLogRepository;
    private final UserRepository userRepository;
    private final ViolationLogRepository violationLogRepository;

    @Transactional
    public ReadingLog createLog(Long userId, ReadingLogDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ReadingLog log = new ReadingLog();
        log.setUser(user);
        log.setTitle(dto.getTitle());
        log.setAuthor(dto.getAuthor());
        log.setDate(dto.getDate());
        log.setTimeSpent(dto.getTimeSpent());
        log.setNotes(dto.getNotes());

        return readingLogRepository.save(log);
    }

    public List<ReadingLog> getUserLogs(Long userId) {
        return readingLogRepository.findByUserId(userId);
    }
    @Transactional
    public void deleteLog(Long userId, Long logId) {
        ReadingLog log = readingLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("Log not found"));

        if (!log.getUser().getId().equals(userId)) {
            throw new SecurityException("You can only delete your own logs");
        }

        readingLogRepository.delete(log);
    }
    // 🔹 查询某个用户的所有阅读日志
    public List<ReadingLog> getAllLogsByUser(Long userId) {
        return readingLogRepository.findByUserId(userId);
    }
    // 🔹 查询某个用户的单个阅读日志
    public ReadingLog getLogById(Long logId, Long userId) {
        return readingLogRepository.findById(logId)
                .filter(log -> log.getUser().getId().equals(userId)) // 确保该日志属于当前用户
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading log not found"));
    }
    // 🔹 更新阅读日志
    public ReadingLog updateLog(Long userId, Long logId, ReadingLogDto dto) {
        // 先检查日志是否存在，并且属于该用户
        ReadingLog log = readingLogRepository.findById(logId)
                .filter(l -> l.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading log not found or unauthorized"));

        // 更新日志内容
        log.setTitle(dto.getTitle());
        log.setAuthor(dto.getAuthor());
        log.setDate(dto.getDate());
        log.setTimeSpent(dto.getTimeSpent());
        log.setNotes(dto.getNotes());

        return readingLogRepository.save(log); // 持久化修改
    }
    public List<ReadingLog> getAllLogs() {
        return readingLogRepository.findAllLogs();
    }
    /**
     * 管理员删除违规日志
     */
    public void deleteInappropriateLog(Long logId) {
        ReadingLog log = null;

        log = readingLogRepository.findById(logId).orElseThrow(() -> new RuntimeException("Log not found"));




        // 记录违规日志删除信息
        ViolationLog violationLog = new ViolationLog(log);
        violationLogRepository.save(violationLog);

        readingLogRepository.delete(log);
    }

}
