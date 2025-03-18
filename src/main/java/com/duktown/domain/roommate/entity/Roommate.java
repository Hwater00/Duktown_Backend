package com.duktown.domain.roommate.entity;

import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.user.entity.User;
import com.duktown.global.type.HallName;
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
public class Roommate { //roomNumber 배정
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "roommate_id")
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

    @OneToMany(fetch = LAZY, mappedBy = "roommate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = LAZY, mappedBy = "roommate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnitUser> unitUsers = new ArrayList<>(); // 같은 유닛 인원

    @Builder.Default
    private boolean assigned = false;  // 배정된 상태를 나타내는 필드 추가

    public void assignRoommate() {
        this.assigned = true;  // 배정되었을 때 호출
    }

    public boolean isFull() {
        return currentPeopleCnt >= occupancy;
    }

    public void addUser(User assignedUser) {
        if (!isFull()) {
            currentPeopleCnt++;
            assignedUser.setRoommate(this);  // 유저에게 룸메이트 배정
            users.add(assignedUser);  // 유저를 룸메이트에 추가
        }
    }


}
