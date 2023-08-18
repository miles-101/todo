package com.mockup.project.todo.util.redis;

import com.mockup.project.todo.content.controller.ContentAPI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RedisService {

    private final RedisRepository redisRepository;

    public void saveCreateTask(ContentAPI.ContentRequest contentRequest) {
        // 레디스에 저장하기.
        String key = "createTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getReservationDateTime();

        redisRepository.saveHash(key, hashKey, contentRequest);
        log.info("레디스 createTask 저장 완료 : {}", contentRequest.toString());
    }

    public void saveDueToAlarmTask(ContentAPI.ContentRequest contentRequest) {
        // 레디스에 저장하기.
        String key = "dueToAlarmTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getEndDateTime();

        redisRepository.saveHash(key, hashKey, contentRequest);
        log.info("레디스 dueToAlarmTask 저장 완료 : {}", contentRequest.toString());
    }
}
