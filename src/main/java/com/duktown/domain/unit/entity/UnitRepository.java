package com.duktown.domain.unit.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit,Long> {

    Optional<Unit> findFirstByOrderByIdDesc();
    List<Unit> findFirst4ByOrderById();

    // 빈 유닛을 조회하는 메소드
    @Query("SELECT u FROM Unit u WHERE u.currentPeopleCnt < u.occupancy ORDER BY u.currentPeopleCnt ASC")
    Optional<Unit> findAvailableUnit();
}
