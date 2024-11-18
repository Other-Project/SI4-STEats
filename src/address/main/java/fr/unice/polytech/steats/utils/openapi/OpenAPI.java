package fr.unice.polytech.steats.utils.openapi;

import java.util.Map;

public record OpenAPI(String openapi, Info info, Server[] servers, Map<String, Map<String, Path>> paths) {
    public record Server(String url) {
    }

    public record Info(String title, String description, String version) {
    }

    public record Path(String[] tags, String summary, String description, Parameter[] parameters) {

        public record Exchange(String description, Map<String, Schema> content) {
            public record Schema() {
            }
        }

        public record Parameter(String name, String in, String description, boolean required, Parameter.Schema schema) {
            public record Schema(String type, String format) {
            }
        }
    }
}
