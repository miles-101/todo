package com.mockup.project.todo.content.scheduler;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.util.MessageUtil;
import com.mockup.project.todo.util.redis.RedisRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
    private final RedisRepository redisRepository;


    @Async
    public void createAddTask(ContentAPI.ContentRequest contentRequest) {

        String key = "createTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getReservationDateTime();

        Date scheduleDate = Timestamp.valueOf(contentRequest.getReservationDateTime());
        contentRequest.clearReservationDateTime();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                restTemplate.postForObject(URL, contentRequest, ContentAPI.ContentResponse.class);
                redisRepository.deleteHash(key, hashKey);
                log.debug("createTask 스케쥴러 실행 완료 및 레디스 삭제: {}", contentRequest.toString());
            }
        }, scheduleDate);

        log.debug("스케쥴러 create 등록 완료 : {}", contentRequest.toString());
        log.debug("등록끝---------------------");
//        return new ContentAPI.ContentResponse(contentRequest.getContent(), contentRequest.getContentDetail(), contentRequest.getStartDateTime(), contentRequest.getEndDateTime());
    }

    @Async
    public void dueToAlarmAddTask(ContentAPI.ContentRequest contentRequest) {

        String key = "dueToAlarmTask";
        String hashKey = contentRequest.getContent() + "_" + contentRequest.getEndDateTime();

        Date scheduleDate = Timestamp.valueOf(contentRequest.getEndDateTime().minusMinutes(60));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageUtil.sendMessages(contentRequest);
                redisRepository.deleteHash(key, hashKey);
                log.debug("dueToAlarmTask 스케쥴러 실행 완료 및 레디스 삭제: {}", contentRequest.toString());
            }
        }, scheduleDate);

        log.debug("스케쥴러 dutoalarm 등록 완료 : {}", contentRequest.toString());
//        return new ContentAPI.ContentResponse(contentRequest.getContent(), contentRequest.getContentDetail(), contentRequest.getStartDateTime(), contentRequest.getEndDateTime());
    }
}
