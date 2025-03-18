package com.duktown.domain.unitUser.controller;

import com.duktown.domain.unitUser.dto.UnitUserDto;
import com.duktown.domain.unitUser.service.UnitUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/unit-user")
public class UnitUserController {

    private final UnitUserService unitUserService;

    // 유닛 대표 지정
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/assign-leader/{unitUserId}")
    public ResponseEntity<Void> assignUnitLeader(@PathVariable Long unitUserId,@RequestBody UnitUserDto unitUserDTO) {
            unitUserService.assignUnitLeader(unitUserId);
            return ResponseEntity.ok().build();
    }

    // 중도 퇴사 처리 -> 유닛유저 타입을 WAITING으로 변경
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/resign/{unitUserId}")
    public ResponseEntity<Void> handleResignation(@PathVariable Long unitUserId,@RequestBody UnitUserDto unitUserDTO) {

            unitUserService.handleResignation(unitUserId);
            return ResponseEntity.ok().build();

    }


}
