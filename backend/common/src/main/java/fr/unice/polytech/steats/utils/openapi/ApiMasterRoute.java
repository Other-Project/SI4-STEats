package fr.unice.polytech.steats.utils.openapi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMasterRoute {
    /**
     * Name of the service
     */
    String name();

    /**
     * Path of the service
     */
    String path();
}
