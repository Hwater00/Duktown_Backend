package com.duktown.domain.unitUser.entity;

import com.duktown.domain.BaseTimeEntity;
import com.duktown.domain.semester.entity.Semester;
import com.duktown.domain.roommate.entity.Roommate;
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
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "roommate_id", nullable = false)
    private Roommate roommate; // 룸 정보

    @Builder.Default
    private int occupancy = 3; // 방 수 제한(12명 제한)
    @Builder.Default
    private  int uintNumber = 1; // 유닛구분 12명이 1개의 유닛 0~8호 중 0~3호/4~8호

    @Enumerated(STRING)
    @Column(nullable = false)
    private UnitUserType unitUserType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "semester_id", nullable = true)
    private Semester semester; // 학기 연관 추가


}
