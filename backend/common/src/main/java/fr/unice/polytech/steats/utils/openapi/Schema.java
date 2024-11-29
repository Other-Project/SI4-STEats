package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.utils.JsonResponse;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Schema(String $ref, String type, String format, String title, Map<String, Schema> properties, Schema items, @JsonProperty("enum") List<String> enumValues) {
    public static final String REF_PREFIX = "#/components/schemas/";

    private Schema(String type, String format) {
        this(null, type, format, null, null, null, null);
    }

    public Schema(Map<String, Schema> properties, String title) {
        this(null, "object", null, title, properties, null, null);
    }

    private Schema(Schema items) {
        this(null, "array", null, null, null, items, null);
    }

    private Schema(String refClass) {
        this(REF_PREFIX + refClass, null, null, null, null, null, null);
    }

    public static SchemaDefinition getSchema(Class<?> type) {
        if (type.isArray()) {
            var childSchemaDef = getSchema(type.getComponentType());
            return new SchemaDefinition(new Schema(childSchemaDef.refSchema), childSchemaDef.declaredSchema);
        }

        if (type.isEnum())
            return new SchemaDefinition(new Schema(null, "string", null, null, null, null, Arrays.stream(type.getEnumConstants()).map(Object::toString).toList()));
        if (type == LocalDate.class)
            return new SchemaDefinition(new Schema("string", "date"));
        if (type == LocalDateTime.class)
            return new SchemaDefinition(new Schema("string", "date-time"));
        if (type == String.class)
            return new SchemaDefinition(new Schema("string", null));


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
            var fieldSchemaDef = getSchema(field.getGenericType());
            properties.put(field.getName(), fieldSchemaDef.refSchema);
            declaredSchema.add(fieldSchemaDef.declaredSchema);
        }
        return new SchemaDefinition(type.getName(), new Schema(properties, type.getSimpleName()), declaredSchema);
    }

    public static SchemaDefinition getSchema(Type type) {
        try {
            if (!(type instanceof ParameterizedType parameterizedType)) return getSchema(Class.forName(type.getTypeName()));
            String typeName = parameterizedType.getRawType().getTypeName();
            if (Objects.equals(typeName, JsonResponse.class.getTypeName()))
                return getSchema(parameterizedType.getActualTypeArguments()[0]);
            if (Objects.equals(typeName, List.class.getTypeName())) {
                var childSchemaDef = getSchema(parameterizedType.getActualTypeArguments()[0]);
                return new SchemaDefinition(new Schema(childSchemaDef.refSchema), childSchemaDef.declaredSchema);
            }
            return getSchema(Class.forName(typeName));
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
            this(new Schema(name), Stream.concat(Stream.of(Map.entry(name, declaredSchema)), childrenSchema == null ? Stream.empty() : childrenSchema.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
    }
}
