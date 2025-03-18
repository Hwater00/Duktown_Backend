package com.duktown.domain.unitUser.entity;

import com.duktown.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitUserRepository extends JpaRepository<UnitUser,Long> {

    Optional<UnitUser> findByUserId(Long userId);
    List<UnitUser> findFirst11ByOrderByCreatedAtAsc();

    @Query("SELECT u FROM User u WHERE u.deleted = false AND EXISTS (" +
            "SELECT 1 FROM UnitUser uu WHERE uu.user = u AND uu.semester.endDate > CURRENT_DATE) " +
            "ORDER BY u.createdAt ASC")
    Optional<User> findNewUserToAssign();

}
