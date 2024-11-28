package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Parameter(String name, String in, String description, boolean required, Schema schema, String example) {
    @SuppressWarnings({"java:S1144"}) // False positive
    private Parameter(String name, String in, String description, boolean required, Class<?> parameterType, String example) {
        this(name, in, description.isBlank() ? null : description, required, parameterType, new Schema(parameterType), example.isBlank() ? null : example);
    }

    private Parameter(String name, String in, String description, boolean required, Class<?> parameterType, Schema schema, String example) {
        this(name, in, description, required, schema.shouldBeRef() ? new Schema(parameterType.getSimpleName()) : schema, example);
    }

    public Parameter(Class<?> parameterType, ApiPathParam pathParam) {
        this(pathParam.name(), "path", pathParam.description(), true, parameterType, pathParam.example());
    }

    public Parameter(Class<?> parameterType, ApiQueryParam queryParam) {
        this(queryParam.name(), "query", queryParam.description(), false, parameterType, queryParam.example());
    }
}
