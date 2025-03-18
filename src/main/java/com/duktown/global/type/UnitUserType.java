package com.duktown.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UnitUserType {
    // 유닛장, 유닛원
    UNIT_LEADER,
    UNIT_MEMBER,

    // 만료된 유닛_아직 유닛 설정 전
    WAITING
}
