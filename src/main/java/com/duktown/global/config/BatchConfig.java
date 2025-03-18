package com.duktown.global.config;

import com.duktown.global.config.batch.DormCertSemesterBatchConfig;
import com.duktown.global.config.batch.UnitAssignmentBatchConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    // 두 Job을 순차적으로 실행하는 Job 설정
    @Bean
    public Job sequentialJob(DormCertSemesterBatchConfig dormCertSemesterBatchConfig,
                             UnitAssignmentBatchConfig unitAssignmentBatchConfig) {
        // dormCertJob Step 완료 후 unitAssignmentJob Step 실행
        return jobBuilderFactory.get("sequentialJob")
                .start(dormCertSemesterBatchConfig.dormCertStep(null, null, null))  // 첫 번째 배치 작업
                .next(unitAssignmentBatchConfig.unitAssignmentStep(null, null, null))  // 두 번째 배치 작업
                .build();
    }
}