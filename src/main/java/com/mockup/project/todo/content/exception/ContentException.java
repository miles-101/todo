package com.mockup.project.todo.content.exception;

public class ContentException extends RuntimeException{
    // TODO stacktrace 추가하기
    private int errorCode;
    public ContentException(int errorCode,String message) {
        super(message);
    }

    public ContentException(String message){
        super(message);
    }
}
