package com.mockup.project.todo;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.content.scheduler.ContentScheduler;
import com.mockup.project.todo.util.JsonMapper;
import com.mockup.project.todo.util.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RedisRepository redisRepository, ContentScheduler contentScheduler, JsonMapper jsonMapper) {
        return args -> {

            String key = "createTask";

            List<Object> redisData = redisRepository.getHashData(key);

            for (Object value : redisData) {
                log.debug("Scheduled task createTask executed: " + value);
                ContentAPI.ContentRequest contentRequest = jsonMapper.convertValue(value, ContentAPI.ContentRequest.class);
                contentScheduler.createAddTask(contentRequest);
            }

            key = "dueToAlarmTask";
            redisData = redisRepository.getHashData(key);

            for (Object value : redisData) {
                log.debug("Scheduled task dueToAlarmTask executed: " + value);
                ContentAPI.ContentRequest contentRequest = jsonMapper.convertValue(value, ContentAPI.ContentRequest.class);
                contentScheduler.dueToAlarmAddTask(contentRequest);
            }
        };
    }

}
