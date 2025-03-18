//package com.duktown.domain.penaltyPoints.entity;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//import java.time.LocalDate;
//
//@Component
//@RequiredArgsConstructor
//public class PenaltyPointsInitDB {
//
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
//        public void createInit() {
//            PenaltyPoints demoPenaltyPoints = PenaltyPoints.builder()
//                    .date(LocalDate.of(2024, 1, 16))
//                    .score(1)
//                    .reason("대열물 반납일로부터 7일(휴일 제외)이상 연체")
//                    .build();
//
//            PenaltyPoints demoPenaltyPoints2 = PenaltyPoints.builder()
//                    .date(LocalDate.of(2023, 11, 19))
//                    .score(3)
//                    .reason("청소 당번이 청소를 하지 않은 경우")
//                    .build();
//            PenaltyPoints demoPenaltyPoints3 = PenaltyPoints.builder()
//                    .date(LocalDate.of(2024, 1, 8))
//                    .score(3)
//                    .reason("외박 신청서 미작성")
//                    .build();
//
//            em.persist(demoPenaltyPoints);
//            em.persist(demoPenaltyPoints2);
//            em.persist(demoPenaltyPoints3);
//        }
//    }
//}
