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
}
