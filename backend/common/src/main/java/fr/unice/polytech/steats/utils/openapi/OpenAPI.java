package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAPI(String openapi, Info info, List<Server> servers, Map<String, Map<String, Path>> paths, Schemas components) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Server(String url) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Info(String title, String description, String version) {
    }

    public record Schemas(Map<String, Schema> schemas) {
    }
}
