package com.group20.dailyreadingtracker.readinglog;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
    // 在deleteLog方法中添加管理员权限检查
    @Transactional
    public void deleteLog(long userId, long logId) {
        ReadingLog log = readingLogRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("Log not found"));

        // 如果是管理员，直接允许删除
        if (isAdmin(userId)) {
            readingLogRepository.delete(log);
            return;
        }

        // 普通用户只能删除自己的日志
        if (!log.getUser().getId().equals(userId)) {
            throw new SecurityException("You can only delete your own logs");
        }
        readingLogRepository.delete(log);
    }
    // 检查用户是否为管理员
    private boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    // 🔹 查询某个用户的所有阅读日志
    public List<ReadingLog> getAllLogsByUser(Long userId) {
        return readingLogRepository.findByUserId(userId);
    }
    // 🔹 查询某个用户的单个阅读日志
    public ReadingLog getLogById(Long logId, Long userId) {
        return readingLogRepository.findById(logId)
                .filter(log -> log.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading log not found or unauthorized"));
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = authentication.getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found with email: " + email));

        //  确保权限检查正确
        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete logs");
        }

        ReadingLog log = readingLogRepository.findById(logId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reading log not found"));

        readingLogRepository.delete(log);
    }


}
