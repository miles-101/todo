package com.mockup.project.todo.util;

import com.mockup.project.todo.content.controller.ContentAPI;
import com.mockup.project.todo.util.kafka.KafkaMessagesProducer;
import com.mockup.project.todo.util.kafka.MessageType;
import com.mockup.project.todo.util.naver.NaverSms;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class MessageUtil {

    private final JavaMailSender javaMailSender;
    private final KafkaMessagesProducer kafkaMessagesProducer;
    private final NaverSms naverSms;

    public void sendSlackMessage(ContentAPI.ContentRequest contentRequest) {
        log.info("slack message : {}", contentRequest.toString());

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = new HashMap<>();
        request.put("username", "todo alarm bot");
        request.put("text", contentRequest.toMessage());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request);
        String slackChanelUrl = "https://hooks.slack.com/services/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";

        restTemplate.exchange(slackChanelUrl, org.springframework.http.HttpMethod.POST, entity, String.class);
    }

    public void sendEmailMessage(ContentAPI.ContentRequest contentRequest) {

        log.info("email message : {}", contentRequest.toString());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("miles@101.inc");
        message.setTo("ubs4939@naver.com");
        message.setSubject("todo alarm : " + contentRequest.getContent() + "마감 한시간 전입니다.");
        message.setText(contentRequest.toMessage());
        javaMailSender.send(message);
    }

    public void sendNaverMessage(ContentAPI.ContentRequest contentRequest) {
        log.info("naver message : {}", contentRequest.toString());
        try {
            naverSms.sendMessage("01093892230", contentRequest);
        } catch (Exception e) {
            log.error("sms 전송 실패 : {}", e.getMessage());
        }

    }

    public void sendMessages(ContentAPI.ContentRequest contentRequest){
        sendSlackMessage(contentRequest);
        kafkaMessagesProducer.sendKafkaMessages(MessageType.SLACK_SENT, contentRequest);
        sendEmailMessage(contentRequest);
        kafkaMessagesProducer.sendKafkaMessages(MessageType.EMAIL_SENT, contentRequest);
//        sendNaverMessage(contentRequest);
        kafkaMessagesProducer.sendKafkaMessages(MessageType.SMS_SENT, contentRequest);
    }

}
