package fr.unice.polytech.steats.utils.openapi;

import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.lang.reflect.Method;
import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OpenAPIGenerator {
    private static final List<String> HTTP_METHODS = List.of("get", "put", "post", "patch", "delete");

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

    @SuppressWarnings("java:S135")
    public static OpenAPI generate(OpenAPI.Server server, Class<?>... handlers) {
        Map<String, Map<String, Path>> routes = new TreeMap<>();
        Map<String, Schema> schemas = new TreeMap<>();
        for (var handler : handlers) {
            if (!handler.isAnnotationPresent(ApiMasterRoute.class)) continue;
            var handlerAnnotation = handler.getAnnotation(ApiMasterRoute.class);
            for (var method : getMethods(handler)) {
                if (!method.isAnnotationPresent(ApiRoute.class)) continue;
                var routeAnnotation = method.getAnnotation(ApiRoute.class);

                String urlPath = handlerAnnotation.path() + routeAnnotation.path();
                routes.putIfAbsent(urlPath, new TreeMap<>(Comparator.comparingInt(HTTP_METHODS::indexOf)));
                if (routes.get(urlPath).containsKey(routeAnnotation.method().toLowerCase())) continue;

                Map<String, Schema> bodyFields = new HashMap<>();
                List<Parameter> parameters = Arrays.stream(method.getParameters())
                        .map(parameter -> {
                            if (parameter.isAnnotationPresent(ApiPathParam.class))
                                return new Parameter(parameter.getParameterizedType(), parameter.getAnnotation(ApiPathParam.class));
                            else if (parameter.isAnnotationPresent(ApiQueryParam.class))
                                return new Parameter(parameter.getParameterizedType(), parameter.getAnnotation(ApiQueryParam.class));
                            else if (parameter.isAnnotationPresent(ApiBodyParam.class)) {
                                var bodyParam = parameter.getAnnotation(ApiBodyParam.class);
                                var schema = Schema.getSchema(parameter.getParameterizedType());
                                if (bodyFields.containsKey("")) throw new IllegalArgumentException("Multiple unnamed body parameters are forbidden");
                                if (bodyParam.name().isBlank() && !bodyFields.isEmpty())
                                    throw new IllegalArgumentException("When using unnamed body parameter, no other body parameter should be present");
                                bodyFields.put(bodyParam.name(), schema.refSchema());
                                schemas.putAll(schema.declaredSchema());
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();

                parameters.stream().map(Parameter::schemaToDefine).forEach(schemas::putAll);
                Path.RequestBody requestBody = bodyFields.isEmpty() ? null : new Path.RequestBody(Map.of(
                        "application/json", new Path.Content(bodyFields.containsKey("") ? bodyFields.get("") : new Schema(bodyFields, null, null))
                ));

                Schema.SchemaDefinition responseSchema = Schema.getSchema(method.getGenericReturnType());
                if (responseSchema != null) schemas.putAll(responseSchema.declaredSchema());

                Path path = new Path(
                        List.of(handlerAnnotation.name()),
                        routeAnnotation.summary().isBlank() ? null : routeAnnotation.summary(),
                        routeAnnotation.description().isBlank() ? null : routeAnnotation.description(),
                        parameters,
                        requestBody,
                        getResponses(method, routeAnnotation, responseSchema)
                );
                routes.get(urlPath).put(routeAnnotation.method().toLowerCase(), path);
            }
        }

        OpenAPI.Info info = new OpenAPI.Info("STEats", "STEats API", "1.0.0");
        return new OpenAPI("3.1.0", info, List.of(server), routes, new OpenAPI.Schemas(schemas));
    }

    private static Map<String, Path.Response> getResponses(Method method, ApiRoute routeAnnotation, Schema.SchemaDefinition responseSchema) {
        return Stream.concat(Stream.of(Map.entry(
                method.getReturnType() == void.class ? HttpUtils.NO_CONTENT_CODE : routeAnnotation.successStatus(),
                new Path.Response("Success", responseSchema == null ? null : Map.of("application/json", new Path.Content(responseSchema.refSchema())))
        )), Arrays.stream(method.getExceptionTypes()).map(exception -> {
            if (exception == NotFoundException.class) return Map.entry(404, new Path.Response("Not Found", null));
            if (exception == ConnectException.class) return Map.entry(502, new Path.Response("Connection to sub-service failed", null));
            return Map.entry(500, new Path.Response("An error occurred while processing the request", null));
        })).collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Map.Entry::getValue));
    }
}
