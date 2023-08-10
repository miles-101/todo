package com.mockup.project.todo.util.naver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public class NaverSmsDTO {

    @AllArgsConstructor
    @Getter
    public static class Message{
        String to;
        String content;
    }

    @Builder
    @Getter
    public static class MessageRequest {
        String type;
        String contentType;
        String countryCode;
        String from;
        String content;
        List<Message> messages;

    }

    @ToString
    public static class MessageResponse {
        String requestId;
        String requestTime;

        String statusCode;

        String statusName;
    }
}

