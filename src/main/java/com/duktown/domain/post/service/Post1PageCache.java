package com.duktown.domain.post.service;

import com.duktown.domain.post.entity.Post;
import com.duktown.domain.post.entity.PostRepository;
import com.duktown.global.type.Category;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class Post1PageCache {
    private Slice<Post> page1;
    private final PostRepository postRepository;

    @SchedulerLock(name = "cron_lock", lockAtLeastFor = "20s", lockAtMostFor = "50s")
    @Scheduled(cron ="30 * * * * *")
    @Transactional(readOnly = true)
    public void updateCache(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        page1 = postRepository.findAllByCategory(Category.DAILY, pageable);
    }

    @Transactional(readOnly = true)
    public Slice<Post> getPage1(){
        updateCache();
        return page1;
    }
}
