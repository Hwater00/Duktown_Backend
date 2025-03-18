//package com.duktown.domain.unitUser.entity;
//
//import com.duktown.domain.unit.entity.Unit;
//import com.duktown.domain.user.entity.User;
//import com.duktown.global.type.HallName;
//import com.duktown.global.type.UnitUserType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//
//@Component
//@RequiredArgsConstructor
//public class UnitUserInitDB {
//    private final InitService initService;
//
//    @PostConstruct
//    public void init() {
//        initService.createInit();
//    }
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    public static class InitService {
//        private final EntityManager em;
//
//        private final PasswordEncoder passwordEncoder;
//
//        @Value("${custom.demo.pwd}")
//        private String demoPwd;
//
//        public void createInit() {
//
//            String[] names = {"김덕우", "이덕우", "박덕우", "최덕우", "차덕우", "홍덕우",
//                    "곽태영", "김혜수", "정현조", "홍서연", "전윤하"};
//            String[] loginIds = {"kdukwoo", "ldukwoo", "pdukwoo", "choidukwoo", "chadukwoo", "hdukwoo",
//                    "sharpie1330", "Hwater00", "ariha1982", "seoyeon22", "YoonhaJ"};
//
//            User[] dummyUsers = new User[11];
//            Unit[] dummyUnits = new Unit[4];
//
//            for (int i = 0; i < names.length; i++) {
//                dummyUsers[i] = User.builder()
//                        .name(names[i])                           // 사용자 이름
//                        .email(loginIds[i] + "@duksung.ac.kr")     // 사용자 이메일
//                        .loginId(loginIds[i])
//                        .password(passwordEncoder.encode(demoPwd))         // 암호화된 비밀번호
//                        .build();
//                em.persist(dummyUsers[i]);
//            }
//
//            // 기숙사 정보 초기화
//            for (int i = 0; i < 3; i++) {
//                dummyUnits[i] = Unit.builder()
//                        .hallName(HallName.GAON1)
//                        .floorNumber(1)
//                        .buildingNumber(1)
//                        .roomNumber(100 + i)
//                        .occupancy(4)
//                        .build();
//                em.persist(dummyUnits[i]);
//            }
//
//            // 유닛유저
//            int userIndex = 0;
//            for (int i = 0; i < 3; i++) {
//                for (int j = 0; j < 4; j++) {
//                    if (userIndex >= dummyUsers.length) {
//                        break;
//                    }
//
//                    UnitUser demoUnitUser = UnitUser.builder()
//                            .user(dummyUsers[userIndex])
//                            .unit(dummyUnits[i])
//                            .unitUserType(UnitUserType.UNIT_MEMBER)
//                            .unitNumber(1)
//                            .build();
//                    em.persist(demoUnitUser);
//
//                    userIndex++;
//                }
//            }
//        }
//    }
//}
