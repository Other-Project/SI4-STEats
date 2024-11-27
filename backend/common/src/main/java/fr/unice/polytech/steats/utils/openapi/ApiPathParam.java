package fr.unice.polytech.steats.utils.openapi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPathParam {
    String name();
}
