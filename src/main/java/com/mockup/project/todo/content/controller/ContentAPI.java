package com.mockup.project.todo.content.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ContentAPI {

    @Getter
    @AllArgsConstructor
    public static class ContentRequest {
        @NotBlank
        private String content;
        @NotBlank
        private String contentDetail;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;

        // TODO naming 고치기 중복
        public com.mockup.project.todo.content.service.ContentRequest toContentRequest(){
            return new com.mockup.project.todo.content.service.ContentRequest(content, contentDetail, startDateTime, endDateTime);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ContentResponse {
        @NotBlank
        private String content;
        @NotBlank
        private String contentDetail;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }
}
