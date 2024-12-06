package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.utils.HttpResponse;
import fr.unice.polytech.steats.utils.JsonResponse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Schema Object allows the definition of input and output data types. These types can be objects, but also primitives and arrays.
 *
 * @see <a href="https://tools.ietf.org/html/draft-bhutton-json-schema-00">JSON Schema Specification Draft 2020-12</a>
 * @see <a href="https://swagger.io/specification/#schema-object">Schema Object (Swagger documentation)</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Schema(String $ref, String type, String format, String pattern, String title, Map<String, Schema> properties, Schema additionalProperties, Schema items,
                     @JsonProperty("enum") List<String> enumValues) {
    public static final String REF_PREFIX = "#/components/schemas/";

    private Schema(String type, String format) {
        this(type, format, null);
    }

    private Schema(String type, String format, String pattern) {
        this(null, type, format, pattern, null, null, null, null, null);
    }

    public Schema(Map<String, Schema> properties, Schema additionalProperties, String title) {
        this(null, "object", null, null, title, properties, additionalProperties, null, null);
    }

    private Schema(Schema items) {
        this(null, "array", null, null, null, null, null, items, null);
    }

    private Schema(String refClass) {
        this(REF_PREFIX + refClass, null, null, null, null, null, null, null, null);
    }

    private static SchemaDefinition getSchema(Class<?> type) {
        if (type == void.class) return null;
        if (type == HttpResponse.class) return new SchemaDefinition(null, Map.of());

        if (type.isArray()) {
            var childSchemaDef = getSchema(type.getComponentType());
            assert childSchemaDef != null;
            return new SchemaDefinition(new Schema(childSchemaDef.refSchema), childSchemaDef.declaredSchema);
        }

        if (type.isEnum())
            return new SchemaDefinition(new Schema(null, "string", null, null, null, null, null, null, Arrays.stream(type.getEnumConstants()).map(Object::toString).toList()));
        if (type == LocalDate.class)
            return new SchemaDefinition(new Schema("string", "date"));
        if (type == LocalDateTime.class)
            return new SchemaDefinition(new Schema("string", "date-time"));
        if (type == LocalTime.class)
            return new SchemaDefinition(new Schema("string", "time", "^(?:[01]\\d|2[0-3]):(?:[0-5]\\d):(?:[0-5]\\d)$"));
        if (type == Duration.class)
            return new SchemaDefinition(new Schema("string", null, "^PT((?:[01]\\d|2[0-3])H)?((?:[0-5]\\d)M)?((?:[0-5]\\d)S)?$"));
        if (type == String.class)
            return new SchemaDefinition(new Schema("string", null));


        if (type == Byte.class || type == byte.class)
            return new SchemaDefinition(new Schema("integer", "int8"));
        if (type == Integer.class || type == int.class)
            return new SchemaDefinition(new Schema("integer", "int32"));
        if (type == Long.class || type == long.class)
            return new SchemaDefinition(new Schema("integer", "int64"));
        if (type == Float.class || type == float.class)
            return new SchemaDefinition(new Schema("number", "float"));
        if (type == Double.class || type == double.class)
            return new SchemaDefinition(new Schema("number", "double"));
        if (type == Boolean.class || type == boolean.class)
            return new SchemaDefinition(new Schema("boolean", null));

        Map<String, Schema> properties = new HashMap<>();
        List<Map<String, Schema>> declaredSchema = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            var fieldSchemaDef = getSchema(field.getGenericType());
            properties.put(field.getName(), fieldSchemaDef.refSchema);
            declaredSchema.add(fieldSchemaDef.declaredSchema);
        }
        return new SchemaDefinition(type.getName(), new Schema(properties, null, type.getSimpleName()), declaredSchema);
    }

    public static SchemaDefinition getSchema(Type type) {
        try {
            if (type instanceof Class<?> clazz) return getSchema(clazz);
            if (!(type instanceof ParameterizedType parameterizedType)) return getSchema(Class.forName(type.getTypeName()));

            String typeName = parameterizedType.getRawType().getTypeName();
            Type[] genericTypes = parameterizedType.getActualTypeArguments();

            if (Objects.equals(typeName, JsonResponse.class.getTypeName()))
                return getSchema(genericTypes[0]); // Just decapsulate the container class

            // List and Set essentially (it is false to consider all objects with 1 generic type as arrays, but it should work for our use case)
            if (genericTypes.length == 1) {
                var childSchemaDef = getSchema(genericTypes[0]);
                return new SchemaDefinition(new Schema(childSchemaDef.refSchema), childSchemaDef.declaredSchema); // List of the generic type
            }

            // Map (the general case is also false, but it should work for our use case)
            // Note that the key must be a string, as defined in the specification (https://swagger.io/docs/specification/v3_0/data-models/dictionaries/)
            if (genericTypes.length == 2 && Objects.equals(genericTypes[0].getTypeName(), String.class.getTypeName())) {
                var childSchemaDef = getSchema(genericTypes[1]);
                return new SchemaDefinition(new Schema(null, childSchemaDef.refSchema, null), childSchemaDef.declaredSchema); // Objets with
            }

            return getSchema(Class.forName(typeName)); // This will likely fail most of the time, but since it isn't handled correctly, we'll just try to get as much as possible
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public record SchemaDefinition(Schema refSchema, Map<String, Schema> declaredSchema) {
        public SchemaDefinition(Schema refSchema) {
            this(refSchema, Map.of());
        }

        public SchemaDefinition(String name, Schema declaredSchema, List<Map<String, Schema>> childrenSchema) {
            this(name, declaredSchema, childrenSchema.stream().flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        public SchemaDefinition(String name, Schema declaredSchema, Map<String, Schema> childrenSchema) {
            this(
                    new Schema(name),
                    Stream.concat(Stream.of(Map.entry(name, declaredSchema)), childrenSchema == null ? Stream.empty() : childrenSchema.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
        }
    }
}
