package com.duktown.domain.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.deleted = false and u.id = :id and u.loginId = :loginId")
    Optional<User> findByIdAndLoginId(@Param("id") Long id, @Param("loginId") String loginId);

    @Query("select u from User u where u.deleted = false and u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.deleted = false and u.loginId = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);

    @Query("select u from User u where u.deleted = false and u.id = :id")
    Optional<User> findById(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.roomAssignment = false")
    List<User> findUnassignedUsers();

    @Query("SELECT u FROM User u " +
            "JOIN DormCert d ON u.id = d.user.id " +
            "JOIN UnitUser uu ON u.id = uu.user.id " +
            "JOIN Roommate r ON u MEMBER OF r.users " +  // Roommate의 users 리스트에서 u가 존재하는 경우만 조인
            "WHERE d.studentId = :studentId " +
            "AND uu.id = :unitUserId " +
            "AND r.roomNumber = :roomNumber " +
            "AND u.name = :name")
    Optional<User> findUserByConditions(
            @Param("studentId") String studentId,
            @Param("unitUserId") Long unitUserId,
            @Param("roomNumber") Integer roomNumber,
            @Param("name") String name
    );
}
