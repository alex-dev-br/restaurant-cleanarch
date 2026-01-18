package br.com.techchallenge.restaurant_cleanarch.infra.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {}

    public static String parseToString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
