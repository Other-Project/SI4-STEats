package fr.unice.polytech.steats.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class JacksonUtils {
    private JacksonUtils() {
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return getMapper().writeValueAsString(object);
    }

    public static <T> T fromJson(InputStream json, Class<T> clazz) throws IOException {
        return fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), TypeFactory.defaultInstance().constructType(clazz));
    }

    public static <T extends Map<K, V>, K, V> T mapFromJson(InputStream json) throws IOException {
        return fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), TypeFactory.defaultInstance().constructType(new TypeReference<T>() {
        }));
    }

    public static <T extends List<K>, K> T listFromJson(InputStream json, Class<K> clazz) throws IOException {
        return fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
    }

    public static <T> T fromJson(String json, JavaType type) throws JsonProcessingException {
        if (json == null || json.isBlank()) return null;
        return getMapper().readValue(json, type);
    }
}
