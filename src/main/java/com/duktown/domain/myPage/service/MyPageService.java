package com.duktown.domain.myPage.service;

import com.duktown.domain.myPage.dto.PenaltyPointsDto;
import com.duktown.domain.penaltyPoints.entity.PenaltyPoints;
import com.duktown.domain.penaltyPoints.entity.PenaltyPointsRepository;
import com.duktown.domain.profile.dto.ProfileDto;
import com.duktown.domain.roommate.entity.Roommate;
import com.duktown.domain.roommate.entity.RoommateRepository;
import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.unitUser.entity.UnitUserRepository;
import com.duktown.domain.user.entity.User;
import com.duktown.domain.user.entity.UserRepository;
import com.duktown.global.exception.CustomErrorType;
import com.duktown.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final PenaltyPointsRepository penaltyPointsRepository;
    private final UserRepository userRepository;

    private final RoommateRepository roommateRepository;
    private final UnitUserRepository unitUserRepository;

    // 나의 벌점 내역 조회
    public PenaltyPointsDto.PenaltyPointsListResponseDto getMyPenaltyPoints(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(CustomErrorType.USER_NOT_FOUND));

        // (데모버전용 벌점 조회)
        List<PenaltyPoints> penaltyPoints = penaltyPointsRepository.findAllByOrderByDateDesc();

        //실제 벌점 조회 로직
        //List<PenaltyPoints> penaltyPoints = penaltyPointsRepository.findPenaltyPointsByUser(user);
        List<PenaltyPointsDto.PenaltyPointsResponseDto> penaltyPointsResponseDto
                = penaltyPoints.stream()
                .map(PenaltyPointsDto.PenaltyPointsResponseDto::new)
                .collect(Collectors.toList());

        // 총합 벌점을 쿼리문으로 계산하여 반환
        // 배포용:
        Long totalPenaltyPointsByUser = penaltyPointsRepository.getTotalPenaltyPoints();
        //Long totalPenaltyPointsByUser = penaltyPointsRepository.getTotalPenaltyPointsByUser(user);
        return new PenaltyPointsDto.PenaltyPointsListResponseDto(penaltyPointsResponseDto,totalPenaltyPointsByUser);
    }

    // 나의 유닛 조회
    public ProfileDto.ListResponse getMyUnits(Long userId){

        // 조회한 유저의 유닛정보 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CustomErrorType.USER_NOT_FOUND));
        UnitUser unitUser = unitUserRepository.findByUserId(user.getId()).orElseThrow(() -> new CustomException(CustomErrorType.UNIT_USER_NOT_FOUND));

        List<ProfileDto.UnitUserResponse> unitUsers = new ArrayList<>();

        // 기본 4개 유닛 정보 받아오기 (데모버전용)
        List<Roommate> roommates = roommateRepository.findFirst4ByOrderById();

        for (int i = 0; i < roommates.size() - 1; i++) {
            unitUsers.add(ProfileDto.UnitUserResponse.from(roommates.get(i).getRoomNumber(), roommates.get(i).getUnitUsers()));
        }

        // 4번째 유닛에는 유저 등록
        List<UnitUser> myUnitUsers = roommates.get(roommates.size() - 1).getUnitUsers()
                        .stream().limit(3).collect(Collectors.toList());
        myUnitUsers.add(unitUser);

        unitUsers.add(ProfileDto.UnitUserResponse.from(roommates.get(roommates.size() - 1).getRoomNumber(), myUnitUsers));

        return new ProfileDto.ListResponse(unitUsers);
    }
}
