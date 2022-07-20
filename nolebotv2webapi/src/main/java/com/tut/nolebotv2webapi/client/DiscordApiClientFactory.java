package com.tut.nolebotv2webapi.client;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Factory
@Slf4j
public class DiscordApiClientFactory {
    @Singleton
    public DiscordApiClient getApiClient() {
        return Feign.builder().client(new OkHttpClient()).encoder(new JacksonEncoder()).logger(new Slf4jLogger()).logLevel(Logger.Level.FULL).decoder(new JacksonDecoder()).target(DiscordApiClient.class, "https://discord.com/api");
    }
}
