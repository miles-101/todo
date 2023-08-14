package com.mockup.project.todo.util.redis;

import com.mockup.project.todo.content.controller.ContentAPI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveCreateTask(ContentAPI.ContentRequest contentRequest) {
        // 레디스에 저장하기.
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = "createTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getReservationDateTime();
        hashOperations.put(key, hashKey, contentRequest);
        log.info("레디스 createTask 저장 완료 : {}", contentRequest.toString());
    }

    public void saveDueToAlarmTask(ContentAPI.ContentRequest contentRequest) {
        // 레디스에 저장하기.
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = "dueToAlarmTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getEndDateTime();
        hashOperations.put(key, hashKey, contentRequest);
        log.info("레디스 dueToAlarmTask 저장 완료 : {}", contentRequest.toString());
    }
}
