package com.duktown.global.config;

import com.duktown.global.config.batch.DormCertSemesterBatchConfig;
import com.duktown.global.config.batch.RoommateAssignmentBatchConfig;
import com.duktown.global.config.batch.UnitUserAssignmentBatchConfig;
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

    // 3가지 Job을 순차적으로 실행하는 Job 설정
    @Bean
    public Job sequentialJob(DormCertSemesterBatchConfig dormCertSemesterBatchConfig,
                             RoommateAssignmentBatchConfig roommateAssignmentBatchConfig,
                             UnitUserAssignmentBatchConfig unitUserAssignmentBatchConfig) {
        return jobBuilderFactory.get("sequentialJob")
                // 첫 번째 배치 작업: dormCertSemesterBatchConfig의 Step 사용
                .start(dormCertSemesterBatchConfig.dormCertStep(
                        dormCertSemesterBatchConfig.dormCertReader(null),
                        dormCertSemesterBatchConfig.dormCertProcessor(),
                        dormCertSemesterBatchConfig.dormCertWriter()))
                .next(roommateAssignmentBatchConfig.roommateAssignmentStep(
                        roommateAssignmentBatchConfig.roommateReader(null),
                        roommateAssignmentBatchConfig.roommateProcessor(),
                        roommateAssignmentBatchConfig.roommateWriter()))
                .next(unitUserAssignmentBatchConfig.unitUserUnitAssignmentStep(
                        unitUserAssignmentBatchConfig.unitUserReader(null),
                        unitUserAssignmentBatchConfig.unitUserProcessor(),
                        unitUserAssignmentBatchConfig.unitUserWriter()))
                .build();
    }

}