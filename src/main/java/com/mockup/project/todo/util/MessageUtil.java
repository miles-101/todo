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
        kafkaMessagesProducer.sendKafkaMessages(MessageType.SLACK_SENT, contentRequest);
    }

    public void sendEmailMessage(ContentAPI.ContentRequest contentRequest) {

        log.info("email message : {}", contentRequest.toString());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("miles@101.inc");
        message.setTo("ubs4939@naver.com");
        message.setSubject("todo alarm : " + contentRequest.getContent() + "마감 한시간 전입니다.");
        message.setText(contentRequest.toMessage());
        javaMailSender.send(message);
        kafkaMessagesProducer.sendKafkaMessages(MessageType.EMAIL_SENT, contentRequest);
    }

    public void sendMessageToAdmin(String report) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("miles@101.inc");
        message.setTo("ubs4939@naver.com");
        message.setSubject("todo alarm to admin: 전날 메시지 발송 횟수입니다.");
        message.setText(report);
        javaMailSender.send(message);
    }

    public void sendNaverMessage(ContentAPI.ContentRequest contentRequest) {
        log.info("naver message : {}", contentRequest.toString());
        try {
            naverSms.sendMessage("01093892230", contentRequest);
            kafkaMessagesProducer.sendKafkaMessages(MessageType.SMS_SENT, contentRequest);
        } catch (Exception e) {
            log.error("sms 전송 실패 : {}", e.getMessage());
        }

    }

    public void sendMessages(ContentAPI.ContentRequest contentRequest){
        sendSlackMessage(contentRequest);
        sendEmailMessage(contentRequest);
//        sendNaverMessage(contentRequest);
    }

}
