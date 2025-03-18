package com.duktown.domain.unit.entity;

import com.duktown.domain.semester.entity.Semester;
import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.user.entity.User;
import com.duktown.global.type.HallName;
import com.duktown.global.type.UnitUserType;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Unit {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "unit_id")
    private Long id;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private HallName hallName; // 가온1/가온2/국제

    @Column(nullable = false)
    private Integer floorNumber; // 층

    @Column(nullable = false)
    private Integer buildingNumber; // 동 1=A, 2=B

    @Column(nullable = false)
    private Integer roomNumber; // 호

    @Builder.Default
    private int occupancy = 4; // 수용 인원

    private int currentPeopleCnt; // 현재 인원

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnitUser> unitUsers = new ArrayList<>(); // 같은 유닛 인원

    /**
     * 유닛에 새로운 유저를 배정하는 메소드
     *
     * @param user 배정할 유저
     * @param unitUserType 유저의 타입 (UNIT_LEADER, UNIT_MEMBER 등)
     * @param semester 배정된 학기
     * @return 배정된 유저
     */
    public UnitUser assignUnitUser(User user, UnitUserType unitUserType, Semester semester) {
        if (currentPeopleCnt < occupancy) { // 빈 자리가 있을 경우
            UnitUser unitUser = UnitUser.builder()
                    .user(user)
                    .unit(this)
                    .unitUserType(unitUserType)
                    .semester(semester)
                    .build();
            this.unitUsers.add(unitUser); // 유닛에 유저 추가
            currentPeopleCnt++; // 현재 인원 수 증가
            return unitUser; // 배정된 유저 반환
        }
        return null; // 빈 자리가 없으면 null 반환
    }

}
