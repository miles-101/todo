package com.mockup.project.todo.content.scheduler;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.content.service.ContentService;
import com.mockup.project.todo.util.JsonMapper;
import com.mockup.project.todo.util.MessageUtil;
import com.mockup.project.todo.util.redis.RedisRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ContentScheduler {

    private final MessageUtil messageUtil;
    private final RedisRepository redisRepository;
    private final JsonMapper jsonMapper;
    private final ContentService contentService;

    @Scheduled(cron = "0 * * * * *")
    public void dueToAlarmAddTask() {
        log.info("dueToAlarmAddTask 스케쥴러 실행");
        String key = "dueToAlarmTask";

        List<Object> redisData = redisRepository.getHashData(key);

        for (Object value : redisData) {
            ContentAPI.ContentRequest contentRequest = jsonMapper.convertValue(value, ContentAPI.ContentRequest.class);

            if (Duration.between(LocalDateTime.now(), contentRequest.getEndDateTime()).toMinutes() < 60) {
                messageUtil.sendMessages(contentRequest);
                redisRepository.deleteHash(key, contentRequest.getContent() + "_" + contentRequest.getEndDateTime());
            }
        }

        log.info("dueToAlarmAddTask 스케쥴러 끝");
    }


    @Scheduled(cron = "0 * * * * *")
    public void checkReservation() {
        log.info("checkReservation 스케쥴러 실행");

        String key = "createTask";

        List<Object> redisData = redisRepository.getHashData(key);

        for (Object value : redisData) {

            log.debug("Scheduled task createTask executed: " + value);
            ContentAPI.ContentRequest contentRequest = jsonMapper.convertValue(value, ContentAPI.ContentRequest.class);

            if (LocalDateTime.now().isAfter(contentRequest.getReservationDateTime()) || LocalDateTime.now().isEqual(contentRequest.getReservationDateTime())) {
                contentService.createContent(contentRequest.toContentRequest());
                redisRepository.deleteHash(key, contentRequest.getContent() + "_" + contentRequest.getReservationDateTime());
            }
        }

    }
}
