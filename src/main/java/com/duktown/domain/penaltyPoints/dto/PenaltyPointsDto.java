package com.duktown.domain.penaltyPoints.dto;

import com.duktown.domain.penaltyPoints.entity.PenaltyPoints;
import com.duktown.global.type.PenaltyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PenaltyPointsDto {

    // 특정 사용자 상벌점 내역 및 총점 반환 DTO
    @Getter
    public static class UserPenaltyDto {
        private final List<PenaltyPointsDto.ResponseDto> penaltyPoints;
        private final int totalScore;

        public UserPenaltyDto(List<PenaltyPoints> points, int totalScore) {
            this.penaltyPoints = points.stream().map(PenaltyPointsDto.ResponseDto::new).collect(Collectors.toList());
            this.totalScore = totalScore;
        }
    }

    // 벌점 및 상점 부여 요청 DTO
    @Getter
    @AllArgsConstructor
    public static class BulkRequestDto {
        private List<UserConditionDto> users;
        private String reason;
        private int score;
    }

    // 벌점 및 상점 대상자 조회 조건 DTO
    @Getter
    @AllArgsConstructor
    public static class UserConditionDto {
        private String studentId;
        private Long unitUserId;
        private Integer roomNumber;
        private String name;
    }

    // 벌점 및 상점 응답 DTO
    @Getter
    public static class ResponseDto {
        private Long id;
        private String reason;
        private int score;
        private LocalDate date;
        private PenaltyType penaltyType;

        public ResponseDto(PenaltyPoints penaltyPoints) {
            this.id = penaltyPoints.getId();
            this.reason = penaltyPoints.getReason();
            this.score = penaltyPoints.getScore();
            this.date = penaltyPoints.getDate();
            this.penaltyType = penaltyPoints.getPenaltyType();
        }
    }
}
