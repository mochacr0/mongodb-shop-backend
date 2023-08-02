package com.example.springbootmongodb.config;

import com.example.springbootmongodb.service.OrderService;
import com.example.springbootmongodb.service.UserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Configuration
@NoArgsConstructor
@EnableScheduling
@EnableAsync
public class TaskExecutingConfiguration {
    private final long unverifiedUsersDeletionRateInMilliseconds = 1800000L; //30 minutes
    private final long expiredOrderCancellationRateInMilliseconds = 1800000L;//30 minutes

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private OrderService orderService;

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor buildThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(20);
        taskExecutor.setMaxPoolSize(40);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean(name = "taskScheduler")
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

//    @Scheduled(fixedDelay = expiredOrderCancellationRateInMilliseconds)
//    private void cancelExpiredOrders() {
//        log.info("Running delete unverified users task");
//        orderService.cancelExpiredOrders();
//    }


}
