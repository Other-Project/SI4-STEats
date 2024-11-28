package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAPI(String openapi, Info info, Server[] servers, Map<String, Map<String, Path>> paths) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Server(String url) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Info(String title, String description, String version) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Path(String[] tags, String summary, String description, Parameter[] parameters,
                       RequestBody requestBody, Map<String, Response> responses) {
        
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Parameter(String name, String in, String description, boolean required, Schema schema, String example) {
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record RequestBody(Map<String, Content> content) {
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public record Content(Schema schema) {
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public record Response(String description, Map<String, ResponseContent> content) {
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public record ResponseContent(Schema schema) {
            }
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Schema(String $ref, String type, String format, Map<String, Schema> properties) {
        public Schema(String type, String format) {
            this(null, type, format, null);
        }

        public Schema(Map<String, Schema> properties) {
            this(null, "object", null, properties);
        }

        public Schema(String ref) {
            this("#/components/schemas/" + ref, null, null, null);
        }
    }
}
