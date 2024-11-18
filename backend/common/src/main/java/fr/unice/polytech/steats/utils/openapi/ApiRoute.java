package fr.unice.polytech.steats.utils.openapi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRoute {
    String method();

    String path();

    String summary() default "";

    String description() default "";

    String[] queryParams() default {};

    String[] body() default {};
}
