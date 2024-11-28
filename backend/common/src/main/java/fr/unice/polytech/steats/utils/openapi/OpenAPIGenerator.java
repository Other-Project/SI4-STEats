package fr.unice.polytech.steats.utils.openapi;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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


    public static OpenAPI generate(Class<?>... handlers) {
        Map<String, Map<String, OpenAPI.Path>> routes = new HashMap<>();
        Arrays.stream(handlers)
                .filter(handler -> handler.isAnnotationPresent(ApiMasterRoute.class))
                .flatMap(handler -> getMethods(handler).stream())
                .filter(method -> method.isAnnotationPresent(ApiRoute.class))
                .forEach(method -> {
                    var handlerAnnotation = method.getDeclaringClass().getAnnotation(ApiMasterRoute.class);
                    var routeAnnotation = method.getAnnotation(ApiRoute.class);
                    String urlPath = handlerAnnotation.path() + routeAnnotation.path();
                    String httpMethod = routeAnnotation.method().toLowerCase(Locale.ROOT);

                    routes.putIfAbsent(urlPath, new HashMap<>());
                    if (routes.get(urlPath).containsKey(httpMethod)) return;


                    Map<String, OpenAPI.Schema> requestBody = new HashMap<>();

                    OpenAPI.Path.Parameter[] parameters = Arrays.stream(method.getParameters())
                            .map(parameter -> {
                                if (parameter.isAnnotationPresent(ApiPathParam.class))
                                    return parameterSpec(parameter, parameter.getAnnotation(ApiPathParam.class));
                                else if (parameter.isAnnotationPresent(ApiQueryParam.class))
                                    return parameterSpec(parameter, parameter.getAnnotation(ApiQueryParam.class));
                                else if (parameter.isAnnotationPresent(ApiBodyParam.class)) {
                                    var bodyParam = parameter.getAnnotation(ApiBodyParam.class);
                                    requestBody.put(bodyParam.name(), new OpenAPI.Schema(parameter.getType().getSimpleName().toLowerCase(), parameter.getType().getSimpleName().toLowerCase()));
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .toArray(OpenAPI.Path.Parameter[]::new);

                    OpenAPI.Path path = new OpenAPI.Path(
                            new String[]{handlerAnnotation.name()},
                            routeAnnotation.summary(),
                            routeAnnotation.description(),
                            parameters,
                            requestBody.isEmpty() ? null : new OpenAPI.Path.RequestBody(Map.of("application/json", new OpenAPI.Path.RequestBody.Content(new OpenAPI.Schema(null, "object", "object", requestBody)))),
                            Map.of("200", new OpenAPI.Path.Response("Success", Map.of("application/json", new OpenAPI.Path.Response.ResponseContent(new OpenAPI.Schema("object", "object")))))
                    );
                    routes.get(urlPath).put(routeAnnotation.method().toLowerCase(Locale.ROOT), path);
                });

        OpenAPI.Info info = new OpenAPI.Info("STEats", "STEats API", "1.0.0");
        return new OpenAPI("3.1.0", info, new OpenAPI.Server[]{new OpenAPI.Server("http://localhost:5000")}, routes);
    }

    private static OpenAPI.Path.Parameter parameterSpec(Parameter parameter, ApiPathParam pathParam) {
        return new OpenAPI.Path.Parameter(
                pathParam.name(),
                "path",
                pathParam.description(),
                true,
                new OpenAPI.Schema(parameter.getType().getSimpleName().toLowerCase(), parameter.getType().getSimpleName().toLowerCase())
        );
    }

    private static OpenAPI.Path.Parameter parameterSpec(Parameter parameter, ApiQueryParam queryParam) {
        return new OpenAPI.Path.Parameter(
                queryParam.name(),
                "query",
                queryParam.description(),
                false,
                new OpenAPI.Schema(parameter.getType().getSimpleName().toLowerCase(), parameter.getType().getSimpleName().toLowerCase())
        );
    }
}
