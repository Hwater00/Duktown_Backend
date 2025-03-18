package com.duktown.domain.profile.dto;

import com.duktown.domain.roommate.entity.Roommate;
import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.user.entity.User;
import com.duktown.global.type.HallName;
import com.duktown.global.type.RoleType;
import com.duktown.global.type.UnitUserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ProfileResponse {
        private String name;
        private String email;
        private HallName hallName;
        private Integer buildingNumber;
        private Integer roomNumber;
        private UnitUserType unitUserType;
        private RoleType roleType;

        //TODO: 유닛정보 추가
        public static ProfileResponse from(User user, Roommate roommate, UnitUserType unitUserType) {
            return ProfileResponse.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .hallName(roommate.getHallName())
                    .buildingNumber(roommate.getBuildingNumber())
                    .roomNumber(roommate.getRoomNumber())
                    .unitUserType(unitUserType)
                    .roleType(user.getRoleType())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UnitUserResponse{
        private Integer roomNumber;
        private List<HashMap<String, UnitUserType>> unitUserInfos;

        public static UnitUserResponse from(Integer roomNumber, List<UnitUser> unitUsers) {
            return UnitUserResponse.builder()
                    .roomNumber(roomNumber)
                    .unitUserInfos(unitUsers.stream().map(unitUser -> {
                        HashMap<String, UnitUserType> unitUserInfo = new HashMap<>();
                        unitUserInfo.put(unitUser.getUser().getName(), unitUser.getUnitUserType());
                        return unitUserInfo;
                    }
                    ).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    public static class ListResponse {
        private final List<UnitUserResponse> content;

        public ListResponse(List<UnitUserResponse> content) {
            this.content = content;
        }
    }
}
