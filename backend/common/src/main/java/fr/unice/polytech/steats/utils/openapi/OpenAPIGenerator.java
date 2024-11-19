package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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


    public static String generate(Class<?>... handlers) throws IOException {
        Map<String, Map<String, OpenAPI.Path>> routes = new HashMap<>();
        Pattern urlParamPattern = Pattern.compile("\\{([^/]+)}");
        Arrays.stream(handlers)
                .filter(handler -> handler.isAnnotationPresent(ApiMasterRoute.class))
                .flatMap(handler ->
                        getMethods(handler).stream()
                                .filter(method -> method.isAnnotationPresent(ApiRoute.class))
                                .map(method -> {
                                    var handlerAnnotation = handler.getAnnotation(ApiMasterRoute.class);
                                    var methodAnnotation = method.getAnnotation(ApiRoute.class);
                                    return Map.entry(handlerAnnotation, methodAnnotation);
                                })
                ).forEach(route -> {
                    ApiMasterRoute handlerAnnotation = route.getKey();
                    ApiRoute routeAnnotation = route.getValue();
                    String urlPath = handlerAnnotation.path() + routeAnnotation.path();
                    String method = routeAnnotation.method().toLowerCase(Locale.ROOT);

                    routes.putIfAbsent(urlPath, new HashMap<>());
                    if (routes.get(urlPath).containsKey(method)) return;

                    OpenAPI.Path path = new OpenAPI.Path(
                            new String[]{handlerAnnotation.name()},
                            routeAnnotation.summary(),
                            routeAnnotation.description(),
                            Stream.concat(
                                    Arrays.stream(routeAnnotation.queryParams())
                                            .map(param -> new OpenAPI.Path.Parameter(param, "query", "", false, new OpenAPI.Path.Parameter.Schema("string", "string"))),
                                    urlParamPattern.matcher(urlPath).results()
                                            .map(key -> new OpenAPI.Path.Parameter(key.group(0), "path", "", true, new OpenAPI.Path.Parameter.Schema("string", "string")))
                            ).toArray(OpenAPI.Path.Parameter[]::new)
                    );
                    routes.get(urlPath).put(routeAnnotation.method().toLowerCase(Locale.ROOT), path);
                });

        OpenAPI.Info info = new OpenAPI.Info("STEats", "STEats API", "1.0.0");
        OpenAPI openAPI = new OpenAPI("3.1.0", info, new OpenAPI.Server[]{new OpenAPI.Server("http://localhost:5000")}, routes);

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);
    }
}
