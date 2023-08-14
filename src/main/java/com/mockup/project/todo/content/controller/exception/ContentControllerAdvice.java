package com.mockup.project.todo.content.controller.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mockup.project.todo.content.exception.ContentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestControllerAdvice
@Slf4j
public class ContentControllerAdvice {
    @ExceptionHandler(ContentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String contentException(ContentException e) {
        log.error("ContentException", e);
        return e.getMessage();
    }

    // naver 에러
    @ExceptionHandler({UnsupportedEncodingException.class, NoSuchAlgorithmException.class, URISyntaxException.class, InvalidKeyException.class, JsonProcessingException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String naverException(Exception e) {
        log.error("NaverException", e);
        return e.getMessage();
    }

}
