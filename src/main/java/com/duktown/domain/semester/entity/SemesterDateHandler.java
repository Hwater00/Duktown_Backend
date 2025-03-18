package com.duktown.domain.semester.entity;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SemesterDateHandler {

    /**
     * 실행일을 기준으로 startDate와 endDate를 계산합니다.
     *
     * @param executionDate 실행일
     * @return startDate와 endDate를 담은 배열
     */
    public LocalDate[] calculateSemesterDates(LocalDate executionDate) {
        LocalDate startDate;
        LocalDate endDate;

        // 실행일을 기준으로 처리
        if (executionDate.isBefore(LocalDate.of(executionDate.getYear(), 3, 2))) {
            // 1월 1일 ~ 3월 1일 사이
            startDate = LocalDate.of(executionDate.getYear(), 9, 1); // 9월 1일
            endDate = LocalDate.of(executionDate.getYear(), 12, 31); // 12월 31일
        } else if (executionDate.isBefore(LocalDate.of(executionDate.getYear(), 9, 1))) {
            // 3월 2일 ~ 7월 31일 사이
            startDate = executionDate; // 실행일
            endDate = LocalDate.of(executionDate.getYear(), 7, 31); // 7월 31일
        } else if (executionDate.isBefore(LocalDate.of(executionDate.getYear() + 1, 1, 1))) {
            // 9월 1일 ~ 12월 31일 사이
            startDate = executionDate; // 실행일
            endDate = LocalDate.of(executionDate.getYear(), 12, 31); // 12월 31일
        } else {
            // 그 외 경우는 9월 1일부터 12월 31일까지 (다음 해)
            startDate = LocalDate.of(executionDate.getYear() + 1, 9, 1); // 다음 학기 시작일 (9월 1일)
            endDate = LocalDate.of(executionDate.getYear() + 1, 12, 31); // 12월 31일
        }

        return new LocalDate[] { startDate, endDate };
    }
}