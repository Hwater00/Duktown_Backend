package com.duktown.global.config.batch;

import com.duktown.domain.semester.entity.Semester;
import com.duktown.domain.unit.entity.Unit;
import com.duktown.domain.unitUser.entity.UnitUser;
import com.duktown.domain.unitUser.entity.UnitUserRepository;
import com.duktown.domain.unit.entity.UnitRepository;
import com.duktown.global.type.UnitUserType;
import com.duktown.domain.user.entity.User;
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
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class UnitAssignmentBatchConfig {

    // Job 및 Step 빌드를 위한 의존성 주입
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final UnitUserRepository unitUserRepository;
    private final UnitRepository unitRepository;

    // Job을 정의하여 실행 순서와 흐름을 설정합니다
    @Bean
    public Job unitAssignmentJob(Step unitAssignmentStep) {
        return jobBuilderFactory.get("unitAssignmentJob")
                .start(unitAssignmentStep) // 첫 번째 Step부터 시작
                .build(); // Job 빌드 완료
    }

    // Step을 정의하여 실제 배치 처리 로직을 설정
    @Bean
    public Step unitAssignmentStep(ItemReader<UnitUser> reader,
                                   ItemProcessor<UnitUser, UnitUser> processor,
                                   ItemWriter<UnitUser> writer) {
        return stepBuilderFactory.get("unitAssignmentStep")
                .<UnitUser, UnitUser>chunk(10)  // 10개씩 처리하는 Chunk 기반 처리 설정
                .reader(reader) // 데이터를 읽을 Reader 지정
                .processor(processor) // 데이터 처리 로직을 담당할 Processor 지정
                .writer(writer) // 데이터를 저장할 Writer 지정
                .build(); // Step 빌드 완료
    }

    // UnitUser 데이터를 조회하여 배정된 유저를 읽는 Reader 설정
    @Bean
    public ItemReader<UnitUser> unitUserReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<UnitUser>()
                .name("unitUserReader") // Reader 이름 설정
                .entityManagerFactory(entityManagerFactory) // EntityManagerFactory 설정
                .queryString("SELECT u FROM UnitUser u WHERE u.unitUserType IN :unitUserTypes") // 쿼리문 수정: UNIT_LEADER, UNIT_MEMBER를 모두 조회
                .parameterValues(Map.of("unitUserTypes", List.of(UnitUserType.UNIT_LEADER, UnitUserType.UNIT_MEMBER))) // `UNIT_LEADER`와 `UNIT_MEMBER` 상태인 사용자 조회
                .pageSize(10) // 한 번에 읽을 데이터 수 (페이징 처리)
                .build(); // JpaPagingItemReader 설정 완료
    }

    // 데이터를 처리할 Processor 설정
    @Bean
    public ItemProcessor<UnitUser, UnitUser> unitUserProcessor() {
        return unitUser -> {
            // 만료 조건을 체크하여 만료된 경우 EXPIRED 상태로 변경
            if (isUnitUserExpired(unitUser)) {
                unitUser.setUnitUserType(UnitUserType.WAITING); // 만료 상태로 변경
            } else if (unitUser.getUnitUserType() == UnitUserType.UNIT_MEMBER) {
                // 멤버인 경우 새로운 유저 배정
                assignNewUserToUnit(unitUser); // 유닛에 새로운 유저 배정
            }
            return unitUser; // 처리된 데이터를 반환
        };
    }

    // 만료 조건을 확인하는 로직 (사용자 삭제 여부, 학기 종료 여부)
    private boolean isUnitUserExpired(UnitUser unitUser) {
        User user = unitUser.getUser();
        Semester semester = unitUser.getSemester(); // 유닛에 해당하는 학기 정보 가져오기

        // 유저가 탈퇴한 경우
        if (user.isDeleted()) {
            return true; // 탈퇴한 경우 만료 처리
        }

        // 학기가 종료된 경우
        if (semester != null && semester.getEndDate().isBefore(LocalDate.now())) {
            return true; // 현재 날짜가 학기 종료일 이후인 경우 만료 처리
        }

        return false; // 만료되지 않은 경우
    }

    // 빈 유닛에 새로운 유저를 배정하는 공통 로직
    private void assignNewUserToUnit(UnitUser unitUser) {
        Optional<Unit> availableUnit = unitRepository.findAvailableUnit(); // 빈 유닛 조회
        if (availableUnit.isPresent() && availableUnit.get().getCurrentPeopleCnt() < availableUnit.get().getOccupancy()) {
            // 유효한 유저를 찾기 위한 로직
            Optional<User> newUser = unitUserRepository.findNewUserToAssign(); // 유효한 유저 찾기
            if (newUser.isPresent()) {
                Unit unit = availableUnit.get(); // 빈 유닛 가져오기
                User user = newUser.get(); // 새로운 유저 가져오기

                // 유닛 정보 갱신
                unit.setCurrentPeopleCnt(unit.getCurrentPeopleCnt() + 1); // 유닛에 새로운 유저 배정 시, 현재 인원 수 증가

                // 유닛에 새로운 유저 배정
                unitUser.setUnit(unit); // 빈 유닛에 배정
                unitUser.setUser(user); // 새 유저 설정
                unitUser.setUnitUserType(UnitUserType.UNIT_MEMBER); // 유닛 배정 상태로 설정

                unitUserRepository.save(unitUser); // 새 배정된 유저 저장
                unitRepository.save(unit); // 유닛 정보도 갱신하여 저장
            }
        }
    }

    // 처리된 데이터를 데이터베이스에 저장하는 Writer 설정
    @Bean
    public ItemWriter<UnitUser> unitUserWriter() {
        return new ItemWriter<UnitUser>() {
            @Override
            public void write(List<? extends UnitUser> items){
                // 배정 만료된 유저를 처리하고, 새 유저를 배정하는 로직
                for (UnitUser unitUser : items) {
                    // 만료된 유저는 DB에 업데이트
                    if (unitUser.getUnitUserType() == UnitUserType.WAITING) {
                        unitUserRepository.save(unitUser); // 만료된 유저 업데이트

                        // 빈 유닛에 새로운 유저 배정
                        assignNewUserToUnit(unitUser); // 새 유저 배정
                    }
                }
            }
        };
    }
}
