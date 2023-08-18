package com.mockup.project.todo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonMapper {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String objectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public <T> T convertValue(Object object, Class<T> type) {
        try {
            return objectMapper.convertValue(object, type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
