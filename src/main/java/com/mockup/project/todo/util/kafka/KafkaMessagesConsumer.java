package com.mockup.project.todo.util.kafka;

import com.mockup.project.todo.util.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
@AllArgsConstructor
public class KafkaMessagesConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageUtil messageUtil;

    // TODO kafka 에서 데이터 로그를 어떻게 처리할것인가? 생명 주기는?
    @Scheduled(cron = "0 0 0 * * *")
    public void consumeMessagesOncePerDay() {
        log.info("스프링 스케쥴러");
        KafkaConsumer<String, KafkaMessage> kafkaConsumer = createKafkaConsumer();
        kafkaConsumer.subscribe(Collections.singletonList("messages"));

        int[] slackCountAndLengthSum = {0, 0};
        int[] emailCountAndLengthSum = {0, 0};
        int[] smsCountAndLengthSum = {0, 0};

        ConsumerRecords<String, KafkaMessage> records = kafkaConsumer.poll(Duration.ofMillis(10000));
        for (ConsumerRecord<String, KafkaMessage> record : records) {
            KafkaMessage message = record.value();
            log.info("message type= {}", message.messageType);
            log.info("message content= {}", message.messageContent);

            if (message.messageType.equals(MessageType.SLACK_SENT)) {
                slackCountAndLengthSum[0]++;
                slackCountAndLengthSum[1] += message.messageContent.length();
            } else if (message.messageType.equals(MessageType.EMAIL_SENT)) {
                emailCountAndLengthSum[0]++;
                emailCountAndLengthSum[1] += message.messageContent.length();
            } else {
                smsCountAndLengthSum[0]++;
                smsCountAndLengthSum[1] += message.messageContent.length();
            }
        }

        LocalDateTime now = LocalDateTime.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String key;
        if (slackCountAndLengthSum[0] > 0) {
            key = "SLACK_SENT:" + now.format(formatter);
            valueOperations.set(key, new adminMessage(slackCountAndLengthSum[0], (float) slackCountAndLengthSum[1] / slackCountAndLengthSum[0]));
        }

        if (emailCountAndLengthSum[0] > 0) {
            key = "EMAIL_SENT:" + now.format(formatter);
            valueOperations.set(key, new adminMessage(emailCountAndLengthSum[0], (float) emailCountAndLengthSum[1] / emailCountAndLengthSum[0]));
        }

        if (smsCountAndLengthSum[0] > 0) {
            key = "SMS_SENT:" + now.format(formatter);
            valueOperations.set(key, new adminMessage(smsCountAndLengthSum[0], (float) smsCountAndLengthSum[1] / smsCountAndLengthSum[0]));
        }

        kafkaConsumer.close();

        String messageToAdmin = String.format(
                """
                        전날 메시지 내역입니다.
                        SLACK_SENT: %d회, 평균 메시지 길이: %.2f
                        EMAIL_SENT: %d회, 평균 메시지 길이: %.2f
                        SMS_SENT: %d회, 평균 메시지 길이: %.2f
                                                """
                , slackCountAndLengthSum[0], (float) slackCountAndLengthSum[1] / slackCountAndLengthSum[0]
                , emailCountAndLengthSum[0], (float) emailCountAndLengthSum[1] / emailCountAndLengthSum[0]
                , smsCountAndLengthSum[0], (float) smsCountAndLengthSum[1] / smsCountAndLengthSum[0]
        );
        messageUtil.sendMessageToAdmin(messageToAdmin);

    }

    private KafkaConsumer<String, KafkaMessage> createKafkaConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "recording-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put("spring.json.trusted.packages", "com.mockup.project.todo.util.kafka,com.mockup.project.todo.domain");
        return new KafkaConsumer<>(props);
    }

    @AllArgsConstructor
    @Getter
    private class adminMessage {
        int mount;
        float average;
    }
}
