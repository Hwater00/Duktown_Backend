package com.duktown.domain.semester.controller;

import com.duktown.domain.semester.dto.SemesterExtensionDTO;
import com.duktown.domain.semester.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/semester")
public class SemesterController {

    private final SemesterService semesterService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/extend")
    public ResponseEntity<Void> extendContract(@RequestBody SemesterExtensionDTO semesterExtensionDTO) {
        semesterService.extendContract(semesterExtensionDTO.getUserId(), semesterExtensionDTO.getEndDate());
        return ResponseEntity.ok().build();

    }
}
