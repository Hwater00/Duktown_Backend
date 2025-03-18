package com.duktown.global.config.batch;

import com.duktown.domain.roommate.entity.Roommate;
import com.duktown.domain.roommate.entity.RoommateRepository;
import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.unitUser.entity.UnitUserRepository;
import com.duktown.global.type.UnitUserType;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UnitUserAssignmentBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final UnitUserRepository unitUserRepository;
    private final RoommateRepository roommateRepository;

    @Bean
    public Job unitUserUnitAssignmentJob(Step unitUserUnitAssignmentStep) {
        return jobBuilderFactory.get("unitUserUnitAssignmentJob")
                .start(unitUserUnitAssignmentStep)
                .build();
    }

    @Bean
    public Step unitUserUnitAssignmentStep(ItemReader<UnitUser> reader,
                                           ItemProcessor<UnitUser, UnitUser> processor,
                                           ItemWriter<UnitUser> writer) {
        return stepBuilderFactory.get("unitUserUnitAssignmentStep")
                .<UnitUser, UnitUser>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public ItemReader<UnitUser> unitUserReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<UnitUser>()
                .name("unitUserReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT u FROM UnitUser u WHERE u.unitUserType IN :unitUserTypes AND u.semester.endDate < :currentDate")  // 만료된 유닛유저 조회
                .parameterValues(Map.of("unitUserTypes", List.of(UnitUserType.UNIT_LEADER, UnitUserType.UNIT_MEMBER),
                        "currentDate", LocalDate.now()))  // 현재 날짜 이후 만료된 유닛유저만 필터링
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<UnitUser, UnitUser> unitUserProcessor() {
        return unitUser -> {
            // 만료된 유닛유저 상태 변경 (퇴사 처리)
            if (unitUser.getSemester().getEndDate().isBefore(LocalDate.now())) {
                unitUser.setUnitUserType(UnitUserType.WAITING);  // 만료된 유닛유저 상태로 변경
            }

            return unitUser;
        };
    }

    @Bean
    public ItemWriter<UnitUser> unitUserWriter() {
        return unitUsers -> {
            for (UnitUser unitUser : unitUsers) {
                if (unitUser.getUnitUserType() == UnitUserType.WAITING) {
                    unitUserRepository.save(unitUser);  // 만료된 유닛유저 DB에 저장
                } else {
                    // 유닛유저가 새로 입사했을 때 룸메이트 배정
                    assignNewRoommatesToUnitUser(unitUser);
                }
            }
        };
    }

    private void assignNewRoommatesToUnitUser(UnitUser unitUser) {
        List<Roommate> availableRoommates = roommateRepository.findByAssignedFalse();  // 배정되지 않은 룸메이트 목록을 가져옵니다
        if (availableRoommates.size() >= 3) {
            // 룸메이트 3개를 유닛유저에 배정
            for (int i = 0; i < 3; i++) {
                Roommate roommate = availableRoommates.get(i);
                unitUser.setRoommate(roommate);  // 유닛유저에 룸메이트 배정
                unitUserRepository.save(unitUser);  // 유닛유저 정보 저장
                roommate.setAssigned(true);  // 룸메이트 배정 상태 변경
                roommateRepository.save(roommate);  // 룸메이트 정보 저장
            }
        }
    }
}
