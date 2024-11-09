package fr.unice.polytech.steats.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JaxsonUtils {
    private JaxsonUtils() {
    }

    public static String toJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.writeValueAsString(object);
    }

    public static <T> T fromJson(InputStream json, Class<T> clazz) throws IOException {
        return fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), clazz);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.readValue(json, clazz);
    }
}
