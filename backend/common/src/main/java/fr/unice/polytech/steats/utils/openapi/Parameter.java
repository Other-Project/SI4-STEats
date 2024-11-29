package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.lang.reflect.Type;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Parameter(String name, String in, String description, boolean required, Schema schema, String example, @JsonIgnore Map<String, Schema> schemaToDefine) {
    private Parameter(String name, String in, String description, boolean required, Schema.SchemaDefinition schemaDefinition, String example) {
        this(name, in, description.isBlank() ? null : description, required, schemaDefinition.refSchema(), example.isBlank() ? null : example, schemaDefinition.declaredSchema());
    }

    public Parameter(Type parameterType, ApiPathParam pathParam) {
        this(pathParam.name(), "path", pathParam.description(), true, Schema.getSchema(parameterType), pathParam.example());
    }

    public Parameter(Type parameterType, ApiQueryParam queryParam) {
        this(queryParam.name(), "query", queryParam.description(), false, Schema.getSchema(parameterType), queryParam.example());
    }
}
