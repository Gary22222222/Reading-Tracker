package com.group20.dailyreadingtracker.server;

import com.group20.dailyreadingtracker.dto.ReadingLogDto;
import com.group20.dailyreadingtracker.entity.ReadingLog;
import com.group20.dailyreadingtracker.repository.ReadingLogRepository;
import com.group20.dailyreadingtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadingLogService {
    private final ReadingLogRepository repository;
    private final UserRepository userRepository;

    public ReadingLog createLog(Long userId, ReadingLogDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ReadingLog log = new ReadingLog();
        log.setUser(user);
        log.setTitle(dto.getTitle());
        log.setAuthor(dto.getAuthor());
        log.setDate(dto.getDate());
        log.setTimeSpent(dto.getTimeSpent());
        log.setNotes(dto.getNotes());

        return repository.save(log);
    }
}

