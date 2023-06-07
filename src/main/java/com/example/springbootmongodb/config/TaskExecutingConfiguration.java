package com.example.springbootmongodb.config;

import com.example.springbootmongodb.service.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@Configuration
@NoArgsConstructor
@EnableScheduling
@EnableAsync
public class TaskExecutingConfiguration {
    private final long unverifiedUsersDeletionRateInMilliseconds = 1800000L; //30 minutes
    private UserService userService;

    @Bean
    public ThreadPoolTaskExecutor buildThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(40);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public ThreadPoolTaskScheduler buildThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        taskScheduler.setThreadNamePrefix("Task scheduler");
        return taskScheduler;
    }

//    @Scheduled(fixedDelay = unverifiedUsersDeletionRateInMilliseconds)
//    private void deleteUnverifiedUsers() {
//        log.info("Running delete unverified users task");
//        userService.deleteUnverifiedUsers();
//    }

}
