package com.duktown.domain.penaltyPoints.controller;

import com.duktown.domain.penaltyPoints.dto.PenaltyPointsDto;
import com.duktown.domain.penaltyPoints.service.PenaltyPointsService;
import com.duktown.global.security.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penalty-points")
@RequiredArgsConstructor
public class PenaltyPointsController {

    private final PenaltyPointsService penaltyPointsService;

    // 특정 사용자 상벌점 내역 조회 (총점 포함)
    @GetMapping("")
    public ResponseEntity<PenaltyPointsDto.UserPenaltyDto> getUserPenaltyPoints(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok().body(penaltyPointsService.getUserPenaltyPoints(customUserDetails.getId()));
    }

    // 상점 일괄 부여 (1명부터 다수)
    @PostMapping("/reward")
    @PreAuthorize("hasAnyRole('ROLE_DORM_COUNCIL', 'ROLE_MANAGER')")
    public ResponseEntity<Void> giveRewards(
            @RequestBody PenaltyPointsDto.BulkRequestDto request) {

        penaltyPointsService.giveRewards(request);
        return ResponseEntity.ok().build();
    }

    // 벌점 일괄 부여 (1명 ~ 최대 12명)
    @PostMapping("/penalty")
    @PreAuthorize("hasAnyRole('ROLE_DORM_COUNCIL', 'ROLE_MANAGER')")
    public ResponseEntity<Void> givePenalties(
            @RequestBody PenaltyPointsDto.BulkRequestDto request) {

        penaltyPointsService.givePenalties(request);
        return ResponseEntity.ok().build();
    }
}