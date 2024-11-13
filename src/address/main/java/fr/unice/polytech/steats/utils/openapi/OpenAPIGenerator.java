package fr.unice.polytech.steats.utils.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.unice.polytech.steats.address.AddressHttpHandler;
import fr.unice.polytech.steats.payments.PaymentsHttpHandler;
import fr.unice.polytech.steats.users.UserHttpHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OpenAPIGenerator {
    public static void main(String[] args) throws IOException {
        Class<?>[] handlers = {PaymentsHttpHandler.class, AddressHttpHandler.class, UserHttpHandler.class};

        Map<String, Map<String, OpenAPI.Path>> routes = new HashMap<>();
        Pattern urlParamPattern = Pattern.compile("\\{([^/]+)}");
        Arrays.stream(handlers)
                .filter(handler -> handler.isAnnotationPresent(ApiMasterRoute.class))
                .flatMap(handler ->
                        Stream.concat(Arrays.stream(handler.getDeclaredMethods()),
                                        Arrays.stream(handler.getSuperclass().getDeclaredMethods()))
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
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);

        BufferedWriter writer = new BufferedWriter(new FileWriter("openapi.json"));
        writer.write(json);
        writer.close();
        System.out.println(json);
    }
}
