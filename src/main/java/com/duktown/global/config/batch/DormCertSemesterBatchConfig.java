package com.duktown.global.config.batch;

import com.duktown.domain.dormCert.entity.DormCert;
import com.duktown.domain.dormCert.entity.DormCertRepository;
import com.duktown.domain.semester.entity.Semester;
import com.duktown.domain.semester.entity.SemesterDateHandler;
import com.duktown.domain.semester.entity.SemesterRepository;
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

@Configuration
@RequiredArgsConstructor
public class DormCertSemesterBatchConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job을 생성하는 JobBuilderFactory
    private final StepBuilderFactory stepBuilderFactory; // Step을 생성하는 StepBuilderFactory
    private final DormCertRepository dormCertRepository;
    private final SemesterRepository semesterRepository;
    private final SemesterDateHandler semesterDateHandler; // 학기 날짜 계산기

    // Job을 설정하여 배치 작업을 시작하는 부분
    @Bean
    public Job dormCertJob(Step dormCertStep) {
        return jobBuilderFactory.get("dormCertJob") // Job 이름 설정
                .start(dormCertStep) // Step을 실행
                .build(); // Job 구성 완료
    }

    // Step을 설정하고 처리할 데이터의 Reader, Processor, Writer를 지정하는 부분
    @Bean
    public Step dormCertStep(ItemReader<DormCert> reader,
                             ItemProcessor<DormCert, DormCert> processor,
                             ItemWriter<DormCert> writer) {
        return stepBuilderFactory.get("dormCertStep")
                .<DormCert, DormCert>chunk(10) // 한 번에 10개씩 처리
                .reader(reader) // ItemReader 지정
                .processor(processor) // ItemProcessor 지정
                .writer(writer) // ItemWriter 지정
                .build(); // Step 구성 완료
    }

    // DormCert 인증 대기 중인 데이터를 읽어오는 ItemReader 설정
    @Bean
    public ItemReader<DormCert> dormCertReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<DormCert>()
                .name("dormCertReader") // Reader 이름 설정
                .entityManagerFactory(entityManagerFactory) // JPA EntityManager 설정
                .queryString("SELECT d FROM DormCert d WHERE d.certified IS NULL") // 인증 대기 중인 데이터만 조회
                .pageSize(10) // 한 번에 읽을 데이터 수 설정 (페이징 처리)
                .build(); // JpaPagingItemReader 생성
    }

    // ItemProcessor에서 인증 상태를 검증하고, 인증된 경우 Semester를 생성하는 처리 로직
    @Bean
    public ItemProcessor<DormCert, DormCert> dormCertProcessor() {
        return dormCert -> {
            // 인증 검증
            boolean isCertified = checkDormCert(dormCert.getImgUrl(), dormCert.getStudentId());
            dormCert.setCertified(isCertified); // 인증 상태 설정

            // 인증이 완료된 경우, 학기 정보를 생성하여 저장
            if (isCertified) {
                createSemester(dormCert.getUser());
            }

            return dormCert; // 처리된 DormCert 객체 반환
        };
    }

    // 인증 검증 로직 (이미지 URL과 학생 ID를 확인)
    private boolean checkDormCert(String imgUrl, String studentId) {
        return imgUrl != null && studentId != null; // 이미지 URL과 학생 ID가 모두 존재하는지 확인
    }

    // 인증이 완료된 유저의 Semester를 생성하고 저장하는 로직
    private void createSemester(User user) {
        // 현재 날짜를 기준으로 학기 날짜 계산
        LocalDate executionDate = LocalDate.now(); // 실행일은 현재 날짜
        LocalDate[] semesterDates = semesterDateHandler.calculateSemesterDates(executionDate); // 학기 시작일과 종료일 계산

        // Semester 객체 생성
        Semester semester = Semester.builder()
                .user(user) // 유저 설정
                .startDate(semesterDates[0]) // 계산된 학기 시작일 설정
                .endDate(semesterDates[1])   // 계산된 학기 종료일 설정
                .build();

        semesterRepository.save(semester); // 생성된 Semester를 DB에 저장
    }

    // 인증된 DormCert 목록을 DB에 저장하는 ItemWriter 설정
    @Bean
    public ItemWriter<DormCert> dormCertWriter() {
        return dormCertRepository::saveAll; // 인증된 DormCert들을 DB에 한 번에 저장
    }

}
