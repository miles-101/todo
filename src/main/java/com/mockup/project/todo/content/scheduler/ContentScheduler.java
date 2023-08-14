package com.mockup.project.todo.content.scheduler;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.util.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Component
@AllArgsConstructor
@Slf4j
public class ContentScheduler {

    private static final String URL = "http://localhost:8080/api/v1/content/";
    private final RestTemplate restTemplate = new RestTemplate();
    private final Timer timer;
    private final MessageUtil messageUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public ContentAPI.ContentResponse createAddTask(ContentAPI.ContentRequest contentRequest) {

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = "createTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getReservationDateTime();

        Date scheduleDate = Timestamp.valueOf(contentRequest.getReservationDateTime());
        contentRequest.clearReservationDateTime();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                restTemplate.postForObject(URL, contentRequest, ContentAPI.ContentResponse.class);
                hashOperations.delete(key, hashKey);
                log.info("createTask 스케쥴러 실행 완료 및 레디스 삭제: {}", contentRequest.toString());
            }
        }, scheduleDate);

        log.info("스케쥴러 create 등록 완료 : {}", contentRequest.toString());
        return new ContentAPI.ContentResponse(contentRequest.getContent(), contentRequest.getContentDetail(), contentRequest.getStartDateTime(), contentRequest.getEndDateTime());
    }

    public ContentAPI.ContentResponse dueToAlarmAddTask(ContentAPI.ContentRequest contentRequest) {

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String key = "dueToAlarmTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getEndDateTime();

        Date scheduleDate = Timestamp.valueOf(contentRequest.getEndDateTime().minusMinutes(60));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageUtil.sendMessages(contentRequest);
                hashOperations.delete(key, hashKey);
                log.info("dueToAlarmTask 스케쥴러 실행 완료 및 레디스 삭제: {}", contentRequest.toString());
            }
        }, scheduleDate);

        log.info("스케쥴러 dutoalarm 등록 완료 : {}", contentRequest.toString());
        return new ContentAPI.ContentResponse(contentRequest.getContent(), contentRequest.getContentDetail(), contentRequest.getStartDateTime(), contentRequest.getEndDateTime());
    }
}
