package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * This is the root object of the OpenAPI document.
 *
 * @param openapi    This string MUST be the version number of the OpenAPI Specification that the OpenAPI document uses.
 * @param info       Provides metadata about the API.
 * @param servers    An array of Server Objects, which provide connectivity information to a target server.
 * @param paths      The available paths and operations for the API.
 * @param components An element to hold various schemas for the document.
 * @see <a href="https://swagger.io/specification/#openapi-object">OpenAPI Object (Swagger documentation)</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAPI(String openapi, Info info, List<Server> servers, Map<String, Map<String, Path>> paths, Schemas components) {

    /**
     * An object representing a Server.
     *
     * @param url A URL to the target host.
     * @see <a href="https://swagger.io/specification/#server-object">Server Object (Swagger documentation)</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Server(String url) {
    }

    /**
     * The object provides metadata about the API.
     *
     * @param title       The title of the API.
     * @param description A description of the API. CommonMark syntax MAY be used for rich text representation.
     * @param version     The version of the OpenAPI document (which is distinct from the OpenAPI Specification version or the API implementation version).
     * @see <a href="https://swagger.io/specification/#info-object">Info Object (Swagger documentation)</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Info(String title, String description, String version) {
    }

    /**
     * Holds a set of reusable objects for different aspects of the OAS.
     * All objects defined within the components object will have no effect on the API unless they are explicitly referenced from properties outside the components object.
     *
     * @param schemas An object to hold reusable Schema Objects.
     * @see <a href="https://swagger.io/specification/#components-object">Components Object (Swagger documentation)</a>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Schemas(Map<String, Schema> schemas) {
    }
}
