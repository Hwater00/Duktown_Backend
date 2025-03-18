package com.duktown.domain.unitUser.entity;

import com.duktown.domain.BaseTimeEntity;
import com.duktown.domain.semester.entity.Semester;
import com.duktown.domain.unit.entity.Unit;
import com.duktown.domain.user.entity.User;
import com.duktown.global.type.UnitUserType;
import lombok.*;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Table(name = "unit_users")
public class UnitUser extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "unit_user_id")
    private Long id;  // 유닛구분 12명이 1개의 유닛 0~8호 중 0~3호/4~8호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit; // 룸 정보

    @Builder.Default
    private int occupancy = 3; // 수용 유닛
    private int currentPeopleCnt; // 현재 유닛 갯수

    @Enumerated(STRING)
    @Column(nullable = false)
    private UnitUserType unitUserType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "semester_id", nullable = true)
    private Semester semester; // 학기 연관 추가 (옵션)

    /**
     * 유닛유저에 새로운 유닛을 배정하는 메소드
     *
     * @param user 배정할 유저
     * @param unitUserType 유저의 타입 (UNIT_LEADER, UNIT_MEMBER 등)
     * @param semester 배정된 학기
     * @return 배정된 유저
     */
    public UnitUser assignUnitUser(User user, UnitUserType unitUserType, Semester semester) {
        // 기존 유닛유저가 이미 3개의 유닛을 다 배정받았다면, 새로운 유닛유저 생성
        if (this.currentPeopleCnt >= this.occupancy) {
            return createNewUnitUser(user, unitUserType, semester);
        } else {
            // 유닛유저가 아직 여유가 있으면, 기존 유닛에 배정
            return addExistingUnitUser(user, unitUserType, semester);
        }
    }

    /**
     * 새로운 유닛유저를 생성하고 배정하는 메소드
     */
    private UnitUser createNewUnitUser(User user, UnitUserType unitUserType, Semester semester) {
        // 새로운 유닛유저를 생성하여 배정
        UnitUser newUnitUser = UnitUser.builder()
                .user(user)
                .unit(this.getUnit()) // 새로운 유닛에 배정
                .unitUserType(unitUserType)
                .semester(semester)
                .build();

        // 새로 생성된 유닛유저를 추가하고, 유닛에 3개까지 유저를 배정
        this.addUnitUser(newUnitUser); // 유닛에 유저 추가
        currentPeopleCnt = 1; // 새 유닛유저 추가 후, 인원수 1명으로 설정

        return newUnitUser;
    }

    /**
     * 기존 유닛유저가 여유가 있으면, 유저를 추가 배정하는 메소드
     */
    private UnitUser addExistingUnitUser(User user, UnitUserType unitUserType, Semester semester) {
        // 기존 유닛유저에 유저 추가
        UnitUser existingUnitUser = UnitUser.builder()
                .user(user)
                .unit(this.getUnit()) // 기존 유닛에 배정
                .unitUserType(unitUserType)
                .semester(semester)
                .build();

        // 기존 유닛유저에 유저를 추가
        this.addUnitUser(existingUnitUser); // 유닛에 유저 추가
        currentPeopleCnt++; // 현재 인원 수 증가

        return existingUnitUser;
    }

    /**
     * 유닛에 유닛유저를 추가하는 메소드
     */
    public void addUnitUser(UnitUser unitUser) {
        this.unit.setUnitUsers((List<UnitUser>) unitUser); // 유닛에 유저 추가
        unitUser.setUnit(this.getUnit()); // 양방향 연관 관계 설정
    }
}
