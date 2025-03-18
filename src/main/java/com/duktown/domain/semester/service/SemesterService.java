package com.duktown.domain.semester.service;

import com.duktown.domain.semester.entity.Semester;
import com.duktown.domain.semester.entity.SemesterRepository;
import com.duktown.global.exception.CustomErrorType;
import com.duktown.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository semesterRepository;

    @Transactional
    public void extendContract(Long userId, LocalDate endDate) {
        // 사용자 확인 및 연장 처리
        Semester semester = semesterRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(CustomErrorType.USER_NOT_FOUND));

        semester.setEndDate(endDate);
        semesterRepository.save(semester);
    }
}
