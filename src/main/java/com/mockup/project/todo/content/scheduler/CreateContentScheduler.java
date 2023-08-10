package com.mockup.project.todo.content.scheduler;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.util.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Component
@AllArgsConstructor
@Slf4j
public class CreateContentScheduler {

    // TODO 서버에 이상이 생기면 스케쥴러에 있던 데이터가 모두 날아갈텐데, 어떻게 해야하는가? DB에 넣어서 관리?
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String URL = "http://localhost:8080/api/v1/content/";

    private final Timer timer;
    private final MessageUtil messageUtil;

    public ContentAPI.ContentResponse addTask(ContentAPI.ContentRequest contentRequest){

        Date scheduleDate = Timestamp.valueOf(contentRequest.getReservationDateTime());
        contentRequest.clearReservationDateTime();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                restTemplate.postForObject(URL, contentRequest, ContentAPI.ContentResponse.class);
            }
        }, scheduleDate);

        log.info("스케쥴러 등록 완료 : {}", contentRequest.toString());
        return new ContentAPI.ContentResponse(contentRequest.getContent(), contentRequest.getContentDetail(), contentRequest.getStartDateTime(), contentRequest.getEndDateTime());
    }

    public ContentAPI.ContentResponse dueToAlarmAddTask(ContentAPI.ContentRequest contentRequest){

        Date scheduleDate = Timestamp.valueOf(contentRequest.getEndDateTime().minusMinutes(60));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageUtil.sendMessages(contentRequest);
            }
        }, scheduleDate);

        log.info("스케쥴러 등록 완료 : {}", contentRequest.toString());
        return new ContentAPI.ContentResponse(contentRequest.getContent(), contentRequest.getContentDetail(), contentRequest.getStartDateTime(), contentRequest.getEndDateTime());
    }
}
