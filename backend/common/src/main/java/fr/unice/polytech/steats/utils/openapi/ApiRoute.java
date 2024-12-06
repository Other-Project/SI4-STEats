package fr.unice.polytech.steats.utils.openapi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRoute {
    /**
     * HTTP method of the route
     */
    String method();

    /**
     * Path of the route relative to the master route
     */
    String path();

    /**
     * Summary (title) of the route
     */
    String summary() default "";

    /**
     * Description of the route
     */
    String description() default "";

    /**
     * Success response status code
     */
    int successStatus() default 200;
}
