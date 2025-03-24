package com.group20.dailyreadingtracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReadingLogDto {
    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotNull
    private LocalDate date;

    @Min(1)
    private int timeSpent;

    private String notes;

    public ReadingLogDto(String springBoot, String johnDoe, Object o, int i, String test) {
    }
}

