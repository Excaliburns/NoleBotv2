package com.tut.nolebotv2webapi;

import io.micronaut.runtime.Micronaut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
