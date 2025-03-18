package com.duktown.global.config.batch;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class BatchJobController {

    private final JobLauncher jobLauncher;  // JobLauncher 주입
    private final Job sequentialJob;        // sequentialJob 주입 (두 배치 작업을 순차적으로 실행)

    @GetMapping("/run-batch")
    public String runBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("runDate", new Date())
                    .toJobParameters();

            jobLauncher.run(sequentialJob, jobParameters);
            return "Batch job 성공";
        } catch (JobExecutionException e) {
            e.printStackTrace();
            return "Batch job 실패";
        }
    }
}
