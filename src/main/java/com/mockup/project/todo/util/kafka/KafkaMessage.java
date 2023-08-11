package com.mockup.project.todo.util.kafka;

import com.mockup.project.todo.content.controller.ContentAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class KafkaMessage implements Serializable {

    MessageType messageType;
    String messageContent;
}
