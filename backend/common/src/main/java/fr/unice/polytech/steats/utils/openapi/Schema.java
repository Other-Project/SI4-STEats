package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Schema(String $ref, String type, String format, Map<String, Schema> properties, List<Schema> items) {
    public static final String REF_PREFIX = "#/components/schemas/";

    public Schema(String type, String format) {
        this(null, type, format, null, null);
    }

    public Schema(Map<String, Schema> properties) {
        this(null, "object", null, properties, null);
    }

    public Schema(Class<?> type) {
        this(getSchema(type));
    }

    private Schema(ClassSchema classSchema) {
        this(null, classSchema.type(), classSchema.format(), null, classSchema.items() == null ? null : classSchema.items().stream().map(Schema::new).toList());
    }

    private record ClassSchema(String type, String format, List<ClassSchema> items) {
        public ClassSchema(String type, String format) {
            this(type, format, null);
        }
    }

    public Schema(String refClass) {
        this(REF_PREFIX + refClass, null, null, null, null);
    }

    public boolean shouldBeRef() {
        return type.equals("object");
    }

    private static ClassSchema getSchema(Class<?> type) {
        if (type.isEnum())
            return new ClassSchema("array", null, Arrays.stream(type.getEnumConstants()).map(value -> getSchema(value.getClass())).toList());
        if (type == List.class)
            return new ClassSchema("array", null, type.getTypeParameters().length == 0 ? null : List.of(getSchema(type.getTypeParameters()[0].getClass())));
        if (type.isArray())
            return new ClassSchema("array", null, List.of(getSchema(type.getComponentType())));

        if (type == LocalDate.class)
            return new ClassSchema("string", "date");
        if (type == LocalDateTime.class)
            return new ClassSchema("string", "date-time");
        if (type == String.class)
            return new ClassSchema("string", null);


        if (type == Integer.class || type == int.class)
            return new ClassSchema("integer", "int32");
        if (type == Long.class || type == long.class)
            return new ClassSchema("integer", "int64");
        if (type == Float.class || type == float.class)
            return new ClassSchema("number", "float");
        if (type == Double.class || type == double.class)
            return new ClassSchema("number", "double");
        if (type == Boolean.class || type == boolean.class)
            return new ClassSchema("boolean", null);
        return new ClassSchema("object", null, type.getDeclaredFields().length == 0 ? null : Arrays.stream(type.getDeclaredFields()).map(field -> getSchema(field.getType())).toList());
    }
}
