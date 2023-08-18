package com.mockup.project.todo.content.scheduler;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.content.service.ContentService;
import com.mockup.project.todo.util.JsonMapper;
import com.mockup.project.todo.util.MessageUtil;
import com.mockup.project.todo.util.redis.RedisRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
    private final JsonMapper jsonMapper;
    private final ContentService contentService;

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


    @Async
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
