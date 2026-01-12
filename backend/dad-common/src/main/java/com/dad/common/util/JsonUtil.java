package com.dad.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON Utility
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Object to JSON string
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Object to JSON failed", e);
            throw new RuntimeException("Object to JSON failed", e);
        }
    }

    /**
     * JSON string to object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON to object failed", e);
            throw new RuntimeException("JSON to object failed", e);
        }
    }

    /**
     * JSON string to object (with generic type)
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON to object failed", e);
            throw new RuntimeException("JSON to object failed", e);
        }
    }

    /**
     * Object to JSON string (pretty format)
     */
    public static String toPrettyJson(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Object to JSON failed", e);
            throw new RuntimeException("Object to JSON failed", e);
        }
    }
}
