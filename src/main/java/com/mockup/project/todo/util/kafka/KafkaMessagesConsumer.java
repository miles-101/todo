package com.mockup.project.todo.util.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessagesConsumer {
    //TODO 하루에 한번씩 컨슘해서 레디스에 업로드하도록 변경.
    @KafkaListener(topics = "messages", groupId = "recording-group")
    public void consume(KafkaMessage message) throws Exception {
      log.info("message type= {}", message.messageType);
      log.info("message content= {}", message.messageContent);
    }
}
