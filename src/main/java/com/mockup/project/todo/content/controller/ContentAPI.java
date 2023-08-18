package com.mockup.project.todo.content.controller;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

public class ContentAPI {

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContentRequest {
        @NotBlank
        private String content;
        @NotBlank
        private String contentDetail;
        @PastOrPresent
        private LocalDateTime startDateTime;
        @FutureOrPresent
        private LocalDateTime endDateTime;
        @FutureOrPresent
        private LocalDateTime reservationDateTime;

        public ContentRequest(String content, String contentDetail, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.content = content;
            this.contentDetail = contentDetail;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        // TODO naming 고치기 중복
        public com.mockup.project.todo.content.service.ContentRequest toContentRequest() {
            return new com.mockup.project.todo.content.service.ContentRequest(content, contentDetail, startDateTime, endDateTime, reservationDateTime);
        }


        public String toMessage() {
            return String.format(
                    """
                            "%s" 마감 한시간전입니다.
                            %s
                            서둘러 일을 마무리해주세요!  
                            """,
                    this.content,
                    this.contentDetail
            );
        }
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContentResponse {
        @NotBlank
        private String content;
        @NotBlank
        private String contentDetail;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
    }

}
