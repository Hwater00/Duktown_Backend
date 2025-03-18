package com.duktown.domain.penaltyPoints.service;

import com.duktown.domain.penaltyPoints.dto.PenaltyPointsDto;
import com.duktown.domain.penaltyPoints.entity.PenaltyPoints;
import com.duktown.domain.penaltyPoints.entity.PenaltyPointsRepository;
import com.duktown.domain.user.entity.User;
import com.duktown.domain.user.entity.UserRepository;
import com.duktown.global.exception.CustomException;
import com.duktown.global.type.PenaltyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.duktown.global.exception.CustomErrorType.PENALTY_POINT_USER_NOT_SELECTED;
import static com.duktown.global.exception.CustomErrorType.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PenaltyPointsService {

    private final PenaltyPointsRepository penaltyPointsRepository;
    private final UserRepository userRepository;

    // 특정 사용자의 상벌점 내역 및 총점 조회
    @Transactional(readOnly = true)
    public PenaltyPointsDto.UserPenaltyDto getUserPenaltyPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(USER_NOT_FOUND));

        List<PenaltyPoints> points = penaltyPointsRepository.findPenaltyPointsByUserOrderByDateDesc(user);
        int totalScore = points.stream()
                .mapToInt(p -> p.getPenaltyType() == PenaltyType.PENALTY ? -p.getScore() : p.getScore())
                .sum();

        return new PenaltyPointsDto.UserPenaltyDto(points, totalScore);
    }

    // 상점 부여
    @Transactional
    public void giveRewards(PenaltyPointsDto.BulkRequestDto request) {
        if (request.getUsers().isEmpty()) {
            throw new CustomException(PENALTY_POINT_USER_NOT_SELECTED);
        }

        List<User> users = getUsersByConditions(request.getUsers());
        List<PenaltyPoints> rewards = users.stream()
                .map(user -> PenaltyPoints.builder()
                        .user(user)
                        .reason(request.getReason())
                        .score(request.getScore())
                        .date(LocalDate.now())
                        .penaltyType(PenaltyType.PENALTY)
                        .build())
                .collect(Collectors.toList());

        penaltyPointsRepository.saveAll(rewards);
    }

    // 벌점 부여
    @Transactional
    public void givePenalties(PenaltyPointsDto.BulkRequestDto request) {
        if (request.getUsers().isEmpty()) {
           throw new CustomException(PENALTY_POINT_USER_NOT_SELECTED);
        }

        List<User> users = getUsersByConditions(request.getUsers());
        List<PenaltyPoints> penalties = users.stream()
                .map(user -> PenaltyPoints.builder()
                        .user(user)
                        .reason(request.getReason())
                        .score(request.getScore())
                        .date(LocalDate.now())
                        .penaltyType(PenaltyType.PENALTY)
                        .build())
                .collect(Collectors.toList());

        penaltyPointsRepository.saveAll(penalties);
    }

    // 4가지 조건을 만족하는 User 조회
    private List<User> getUsersByConditions(List<PenaltyPointsDto.UserConditionDto> conditions) {
        return conditions.stream()
                .map(condition -> userRepository.findUserByConditions(
                        condition.getStudentId(),
                        condition.getUnitUserId(),
                        condition.getRoomNumber(),
                        condition.getName()
                ).orElseThrow(() -> new CustomException(USER_NOT_FOUND)))
                .collect(Collectors.toList());
    }
}
