package fr.unice.polytech.steats.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class JaxsonUtils {


    private JaxsonUtils() {
    }

    public static void toJsonStream(Object object, OutputStream stream) throws IOException {
        stream.write( toJson(object).getBytes(StandardCharsets.UTF_8));
        stream.close();
    }
    public static String toJson(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
    }

    public static <T> T fromJson(InputStream json, Class<T> clazz) throws IOException {
        return fromJson(new String(json.readAllBytes(), StandardCharsets.UTF_8), clazz);
    }
    public static <T> T fromJson(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
