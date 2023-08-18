package com.mockup.project.todo.util.redis;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.content.service.ContentRequest;
import com.mockup.project.todo.content.service.ContentResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RedisService {

    private final RedisRepository redisRepository;

    public ContentResponse saveCreateReservation(ContentRequest contentRequest) {
        // 레디스에 저장하기 -> 시간 별로 정렬 -> 시간으로 heap 사용..?
        String key = "createTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getReservationDateTime();
        redisRepository.saveHash(key, hashKey, contentRequest);
        log.info("레디스 createTask 저장 완료 : {}", contentRequest);
        return new ContentResponse(contentRequest.getContent(), contentRequest.getContentDetail(), contentRequest.getStartDateTime(), contentRequest.getEndDateTime());
    }

    public void saveDueToAlarmTask(ContentAPI.ContentRequest contentRequest) {
        // 레디스에 저장하기.
        String key = "dueToAlarmTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getEndDateTime();

        redisRepository.saveHash(key, hashKey, contentRequest);
        log.info("레디스 dueToAlarmTask 저장 완료 : {}", contentRequest);
    }

    public void deleteHash(String key, String hashKey) {
        redisRepository.deleteHash(key, hashKey);
    }

    public void deleteHashByKey(String key) {
        redisRepository.deleteHashByKey(key);
    }
}
