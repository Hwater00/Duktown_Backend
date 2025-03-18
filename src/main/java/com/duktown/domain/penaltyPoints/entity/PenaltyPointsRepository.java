package com.duktown.domain.penaltyPoints.entity;

import com.duktown.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PenaltyPointsRepository extends JpaRepository<PenaltyPoints,Long> {
    // 내 벌점 내역 조회
    List<PenaltyPoints> findPenaltyPointsByUserOrderByDateDesc(User user);





}
