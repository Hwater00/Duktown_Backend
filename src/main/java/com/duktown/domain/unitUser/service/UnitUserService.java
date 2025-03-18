package com.duktown.domain.unitUser.service;

import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.unitUser.entity.UnitUserRepository;
import com.duktown.global.exception.CustomException;
import com.duktown.global.type.UnitUserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.duktown.global.exception.CustomErrorType.UNIT_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UnitUserService {
    private final UnitUserRepository unitUserRepository;

    // 유닛 대표 지정
    @Transactional
    public void assignUnitLeader(Long unitUserId) {
        UnitUser unitUser = unitUserRepository.findById(unitUserId)
                .orElseThrow(() -> new CustomException(UNIT_USER_NOT_FOUND));

        unitUser.setUnitUserType(UnitUserType.UNIT_LEADER);
        unitUserRepository.save(unitUser);
    }

    // 중도 퇴사 처리
    @Transactional
    public void handleResignation(Long unitUserId) {
        UnitUser unitUser = unitUserRepository.findById(unitUserId)
                .orElseThrow(() -> new CustomException(UNIT_USER_NOT_FOUND));

        unitUser.setUnitUserType(UnitUserType.WAITING);
        unitUserRepository.save(unitUser);

    }
}
