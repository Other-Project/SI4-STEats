package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Path(List<String> tags, String summary, String description, List<Parameter> parameters, RequestBody requestBody, Map<String, Response> responses) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RequestBody(Map<String, Content> content) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(String description, Map<String, Content> content) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Content(Schema schema) {
    }
}
