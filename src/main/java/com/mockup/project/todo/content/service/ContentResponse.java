package com.mockup.project.todo.content.service;

import com.mockup.project.todo.content.controller.ContentAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ContentResponse {

    private String content;
    private String contentDetail;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public ContentAPI.ContentResponse toContentAPIResponse(){
       return new ContentAPI.ContentResponse(this.content, this.contentDetail, this.startDateTime, this.endDateTime);
    }
}
