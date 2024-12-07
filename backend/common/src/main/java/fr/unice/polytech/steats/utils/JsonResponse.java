package fr.unice.polytech.steats.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public final class JsonResponse<T> extends HttpResponse {
    public JsonResponse(int statusCode, T response) throws JsonProcessingException {
        super(statusCode, JacksonUtils.toJson(response), Map.of(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON));
    }

    public JsonResponse(T response) throws JsonProcessingException {
        this(HttpUtils.OK_CODE, response);
    }
}
