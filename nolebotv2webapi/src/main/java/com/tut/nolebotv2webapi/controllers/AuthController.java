package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.dtos.DiscordAccessTokenRequestDto;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

import java.util.HashMap;

@Controller("/oauth")
public class AuthController {
    @Property(name = "micronaut.security.oauth2.clients.discord.client-id")
    protected String client_id;

    @Property(name = "micronaut.security.oauth2.clients.discord.client-secret")
    protected String client_secret;

    @Property(name = "micronaut.application.base-ui-url")
    protected String base_ui_url;

    private final DiscordApiClient discordApiClient;

    public AuthController(DiscordApiClient discordApiClient) {
        this.discordApiClient = discordApiClient;
    }

    @Post("/discord")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<DiscordAccessToken> discord(
            final String clientCode
    ) {
        final DiscordAccessToken token = discordApiClient.getAccessToken(
                client_id,
                client_secret,
                "authorization_code",
                clientCode,
                base_ui_url
        ).blockingSingle();

        return HttpResponse.ok(token);
    }
}
