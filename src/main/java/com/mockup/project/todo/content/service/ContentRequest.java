package com.mockup.project.todo.content.service;

import com.mockup.project.todo.content.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ContentRequest {

    private String content;
    private String contentDetail;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime reservationDateTime;


    public Content toContent() {
        return new Content(content, contentDetail, startDateTime, endDateTime, reservationDateTime);
    }
}
