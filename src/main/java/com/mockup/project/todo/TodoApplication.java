package com.mockup.project.todo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner runner(RedisRepository redisRepository, ContentScheduler contentScheduler, JsonMapper jsonMapper) {
//        return args -> {
//
//
//            key = "dueToAlarmTask";
//            redisData = redisRepository.getHashData(key);
//
//            for (Object value : redisData) {
//                log.debug("Scheduled task dueToAlarmTask executed: " + value);
//                ContentAPI.ContentRequest contentRequest = jsonMapper.convertValue(value, ContentAPI.ContentRequest.class);
//                contentScheduler.dueToAlarmAddTask(contentRequest);
//            }
//        };
//    }

}
