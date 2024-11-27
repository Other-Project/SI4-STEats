package fr.unice.polytech.steats.utils.openapi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiBodyParam {
    String name() default "";

    String description() default "";

    boolean required() default true;
}
