package com.duktown.domain.semester.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class SemesterExtensionDTO {
    private Long userId;
    private LocalDate endDate;
}
