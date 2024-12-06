package fr.unice.polytech.steats.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JacksonUtils {
    private JacksonUtils() {
    }

    public static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        return mapper;
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return getMapper().writeValueAsString(object);
    }

    public static ObjectNode toJsonNode(Object object) {
        return getMapper().valueToTree(object);
    }

    public static ArrayNode toJsonNode(Collection<? extends JsonNode> objects) {
        return getMapper().createArrayNode().addAll(objects);
    }

    public static <T> T fromJson(InputStream json, Class<T> clazz) throws IOException {
        return fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), clazz);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return fromJson(json, TypeFactory.defaultInstance().constructType(clazz));
    }

    public static <K, V> Map<K, V> mapFromJson(InputStream json) throws IOException {
        Map<K, V> result = fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), TypeFactory.defaultInstance().constructType(new TypeReference<Map<K, V>>() {
        }));
        if (result == null) return Collections.emptyMap();
        return result;
    }

    public static <T> List<T> listFromJson(InputStream json, Class<T> clazz) throws IOException {
        List<T> result = fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        if (result == null) return Collections.emptyList();
        return result;
    }

    public static <T> T fromJson(String json, JavaType type) throws JsonProcessingException {
        if (json == null || json.isBlank()) return null;
        return getMapper().readValue(json, type);
    }
}
