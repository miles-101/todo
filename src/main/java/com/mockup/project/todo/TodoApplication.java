package com.mockup.project.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.content.scheduler.ContentScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RedisTemplate<String, Object> redisTemplate, ContentScheduler contentScheduler) {
        return args -> {
            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            String key = "createTask";

            // Redis에서 데이터를 가져와서 처리
            hashOperations.entries(key).forEach((hashKey, value) -> {
                log.info("Scheduled task createTask executed: " + value);
                ContentAPI.ContentRequest contentRequest = objectMapper.convertValue(value, ContentAPI.ContentRequest.class);
                contentScheduler.createAddTask(contentRequest);
            });

            key = "dueToAlarmTask";
            hashOperations.entries(key).forEach((hashKey, value) -> {
                log.info("Scheduled task dueToAlarmTask executed: " + value);
                ContentAPI.ContentRequest contentRequest = objectMapper.convertValue(value, ContentAPI.ContentRequest.class);
                contentScheduler.dueToAlarmAddTask(contentRequest);
            });

        };
    }

}
