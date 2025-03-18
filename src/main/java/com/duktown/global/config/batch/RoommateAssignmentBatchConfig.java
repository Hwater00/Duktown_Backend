package com.duktown.global.config.batch;

import com.duktown.domain.roommate.entity.Roommate;
import com.duktown.domain.roommate.entity.RoommateRepository;
import com.duktown.domain.user.entity.User;
import com.duktown.domain.user.entity.UserRepository;
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
import java.util.Iterator;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RoommateAssignmentBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final RoommateRepository roommateRepository;
    private final UserRepository userRepository;

    @Bean
    public Job roommateAssignmentJob(Step roommateAssignmentStep) {
        return jobBuilderFactory.get("roommateAssignmentJob")
                .start(roommateAssignmentStep)
                .build();
    }

    @Bean
    public Step roommateAssignmentStep(ItemReader<Roommate> reader,
                                       ItemProcessor<Roommate, Roommate> processor,
                                       ItemWriter<Roommate> writer) {
        return stepBuilderFactory.get("roommateAssignmentStep")
                .<Roommate, Roommate>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    /**
     * 빈 룸메이트 조회 (현재 인원 < 정원)
     */
    @Bean
    public ItemReader<Roommate> roommateReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Roommate>()
                .name("roommateReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT r FROM Roommate r WHERE r.currentPeopleCnt < r.occupancy")
                .pageSize(10)
                .build();
    }

    /**
     * 룸메이트에 배정되지 않은 유저들을 추가
     */
    @Bean
    public ItemProcessor<Roommate, Roommate> roommateProcessor() {
        return roommate -> {
            if (!roommate.isFull()) {
                // 배정되지 않은 유저 리스트 조회
                List<User> unassignedUsers = userRepository.findUnassignedUsers();
                Iterator<User> userIterator = unassignedUsers.iterator();

                // 룸메이트가 꽉 찰 때까지 반복
                while (!roommate.isFull() && userIterator.hasNext()) {
                    User assignedUser = userIterator.next();
                    roommate.addUser(assignedUser); // 룸메이트에 유저 추가
                    assignedUser.setRoomAssignment(); // 유저 배정 상태 업데이트
                }
            }
            return roommate;
        };
    }

    /**
     * 업데이트된 룸메이트 및 유저 저장
     */
    @Bean
    public ItemWriter<Roommate> roommateWriter() {
        return roommates -> {
            for (Roommate roommate : roommates) {
                roommateRepository.save(roommate);  // 룸메이트 정보 저장
                // 배정된 유저에게 룸메이트 설정
                roommate.getUsers().forEach(user -> {
                    user.setRoommate(roommate);  // 유저에게 룸메이트 배정
                    userRepository.save(user);  // 유저 정보 저장
                });
            }
        };
    }
}
