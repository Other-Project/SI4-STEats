package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Describes a single operation parameter. A unique parameter is defined by a combination of a name and location.
 *
 * @param name           The name of the parameter. Parameter names are case-sensitive.
 * @param in             The location of the parameter. Possible values are "query", "header", "path" or "cookie".
 * @param description    A brief description of the parameter. This could contain examples of use. CommonMark syntax MAY be used for rich text representation.
 * @param required       Determines whether this parameter is mandatory.
 * @param schema         The schema defining the type used for the parameter.
 * @param example        Example of the parameter's potential value. The example SHOULD match the specified schema and encoding properties if present.
 * @param schemaToDefine [INTERNAL] The schemas to later define in the OpenAPI specification.
 * @see <a href="https://swagger.io/specification/#parameter-object">Parameter Object (Swagger documentation)</a>
 */
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
