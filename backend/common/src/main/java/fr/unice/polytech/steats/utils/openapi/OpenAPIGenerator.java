package fr.unice.polytech.steats.utils.openapi;

import java.lang.reflect.Method;
import java.util.*;

public class OpenAPIGenerator {
    private OpenAPIGenerator() {
    }

    private static List<Method> getMethods(Class<?> handler) {
        List<Method> result = new ArrayList<>();
        while (handler != null) {
            result.addAll(Arrays.asList(handler.getDeclaredMethods()));
            handler = handler.getSuperclass();
        }
        return result;
    }

    public static OpenAPI generate(OpenAPI.Server server, Class<?>... handlers) {
        Map<String, Map<String, Path>> routes = new HashMap<>();
        Map<String, Schema> schemas = new HashMap<>();
        Arrays.stream(handlers)
                .filter(handler -> handler.isAnnotationPresent(ApiMasterRoute.class))
                .flatMap(handler -> getMethods(handler).stream())
                .filter(method -> method.isAnnotationPresent(ApiRoute.class))
                .forEach(method -> {
                    var handlerAnnotation = method.getDeclaringClass().getAnnotation(ApiMasterRoute.class);
                    var routeAnnotation = method.getAnnotation(ApiRoute.class);

                    String urlPath = handlerAnnotation.path() + routeAnnotation.path();
                    routes.putIfAbsent(urlPath, new HashMap<>());
                    if (routes.get(urlPath).containsKey(routeAnnotation.method().toLowerCase())) return;

                    Map<String, Schema> bodyFields = new HashMap<>();
                    List<Parameter> parameters = Arrays.stream(method.getParameters())
                            .map(parameter -> {
                                if (parameter.isAnnotationPresent(ApiPathParam.class))
                                    return new Parameter(parameter.getType(), parameter.getAnnotation(ApiPathParam.class));
                                else if (parameter.isAnnotationPresent(ApiQueryParam.class))
                                    return new Parameter(parameter.getType(), parameter.getAnnotation(ApiQueryParam.class));
                                else if (parameter.isAnnotationPresent(ApiBodyParam.class)) {
                                    var bodyParam = parameter.getAnnotation(ApiBodyParam.class);
                                    var schema = new Schema(parameter.getType());
                                    var schemaRef = schema.shouldBeRef();
                                    bodyFields.put(bodyParam.name(), schemaRef ? new Schema(parameter.getType().getSimpleName()) : schema);
                                    if (schemaRef) schemas.putIfAbsent(parameter.getType().getSimpleName(), schema);
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .toList();

                    Path.RequestBody requestBody = bodyFields.isEmpty() ? null : new Path.RequestBody(Map.of(
                            "application/json", new Path.Content(new Schema(bodyFields))
                    ));
                    Schema responseSchema = new Schema(method.getReturnType());
                    if (responseSchema.shouldBeRef()) schemas.putIfAbsent(method.getReturnType().getSimpleName(), responseSchema);
                    Map<String, Path.Response> responses = Map.of(
                            "200", new Path.Response("Success", Map.of("application/json", new Path.Content(responseSchema.shouldBeRef() ? new Schema(method.getReturnType().getSimpleName()) : responseSchema)))
                    );
                    Path path = new Path(
                            List.of(handlerAnnotation.name()),
                            routeAnnotation.summary().isBlank() ? null : routeAnnotation.summary(),
                            routeAnnotation.description().isBlank() ? null : routeAnnotation.description(),
                            parameters,
                            requestBody,
                            responses
                    );
                    routes.get(urlPath).put(routeAnnotation.method().toLowerCase(), path);
                });

        OpenAPI.Info info = new OpenAPI.Info("STEats", "STEats API", "1.0.0");
        return new OpenAPI("3.1.0", info, List.of(server), routes, new OpenAPI.Schemas(schemas));
    }
}
