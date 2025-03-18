package com.duktown.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UnitUserType {
    // 유닛장, 유닛원
    UNIT_LEADER,
    UNIT_MEMBER,
    WAITING, // 퇴사 처리

}
