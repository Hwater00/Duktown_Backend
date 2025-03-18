package com.duktown.domain.roommate.service;

import com.duktown.domain.roommate.entity.RoommateRepository;
import com.duktown.domain.unitUser.entity.UnitUserRepository;
import com.duktown.domain.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoommateService {

    private final RoommateRepository roommateRepository;
    private final UserRepository userRepository;
    private final UnitUserRepository unitUserRepository;

}
