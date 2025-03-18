package com.duktown.domain.roommate.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoommateRepository extends JpaRepository<Roommate,Long> {

    Optional<Roommate> findFirstByOrderByIdDesc();
    List<Roommate> findFirst4ByOrderById();
    List<Roommate> findByAssignedFalse();

}
