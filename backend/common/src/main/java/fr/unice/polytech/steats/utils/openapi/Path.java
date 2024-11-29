package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * Describes a single API operation on a path.
 *
 * @param tags        A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier.
 * @param summary     A short summary of what the operation does.
 * @param description A verbose explanation of the operation behavior. CommonMark syntax MAY be used for rich text representation.
 * @param parameters  A list of parameters that are applicable for this operation.
 * @param requestBody The request body applicable for this operation.
 * @param responses   The list of possible responses as they are returned from executing this operation.
 * @see <a href="https://swagger.io/specification/#operation-object">Operation Object (Swagger documentation)</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Path(List<String> tags, String summary, String description, List<Parameter> parameters, RequestBody requestBody, Map<String, Response> responses) {

    /**
     * Describes a single request body.
     *
     * @param content The content of the request body. The key is a media type or media type range and the value describes it.
     *                For requests that match multiple keys, only the most specific key is applicable. e.g. text/plain overrides text/*
     * @see <a href="https://swagger.io/specification/#request-body-object">Request Body Object (Swagger documentation)</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RequestBody(Map<String, Content> content) {
    }

    /**
     * Describes a single response from an API Operation, including design-time, static links to operations based on the response.
     *
     * @param description A description of the response. CommonMark syntax MAY be used for rich text representation.
     * @param content     A map containing descriptions of potential response payloads. The key is a media type or media type range and the value describes it.
     *                    For responses that match multiple keys, only the most specific key is applicable. e.g. text/plain overrides text/*
     * @see <a href="https://swagger.io/specification/#response-object">Response Object (Swagger documentation)</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(String description, Map<String, Content> content) {
    }

    /**
     * Each Media Type Object provides schema and examples for the media type identified by its key.
     *
     * @param schema The schema defining the content of the request, response, or parameter.
     * @see <a href="https://swagger.io/specification/#media-type-object">Media Type Object (Swagger documentation)</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Content(Schema schema) {
    }
}
