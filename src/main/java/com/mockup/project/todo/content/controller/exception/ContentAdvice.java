package com.mockup.project.todo.content.controller.exception;

import com.mockup.project.todo.content.exception.ContentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ContentAdvice {
    @ExceptionHandler(ContentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String contentException(ContentException e){
        log.error("ContentException", e);
        return e.getMessage();
    }
}
