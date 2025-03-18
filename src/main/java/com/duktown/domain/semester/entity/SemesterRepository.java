package com.duktown.domain.semester.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester,Long> {
    Optional<Semester> findByUserId(Long userId);
}
