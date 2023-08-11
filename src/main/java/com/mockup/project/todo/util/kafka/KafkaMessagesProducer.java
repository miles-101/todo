package com.mockup.project.todo.util.kafka;

import com.mockup.project.todo.content.controller.ContentAPI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class KafkaMessagesProducer {

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    public void sendKafkaMessages(MessageType messageType,ContentAPI.ContentRequest contentRequest){
        log.info("contentRequest = {}", contentRequest);

        kafkaTemplate.send("messages", new KafkaMessage(messageType, contentRequest.toMessage()));
    }
}
