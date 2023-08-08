package com.mockup.project.todo.content.entity;

import com.mockup.project.todo.content.service.ContentRequest;
import com.mockup.project.todo.content.service.ContentResponse;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Content {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    private String content;
    @NotNull
    private String contentDetail;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public Content(String content, String contentDetail, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.content = content;
        this.contentDetail = contentDetail;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public ContentResponse toContentResponse(){
        return new ContentResponse(content, contentDetail, startDateTime, endDateTime);
    }

    public void updateContent(ContentRequest contentRequest) {
        this.content = contentRequest.getContent();
        this.contentDetail = contentRequest.getContentDetail();
        this.startDateTime = contentRequest.getStartDateTime();
        this.endDateTime = contentRequest.getEndDateTime();
    }
}
