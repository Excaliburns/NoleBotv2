package com.tut.nolebotv2webapi.controllers;


import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

@Controller("/oauth")
public class AuthController {
    @Property(name = "micronaut.security.oauth2.clients.discord.client-id")
    protected String clientId;

    @Property(name = "micronaut.security.oauth2.clients.discord.client-secret")
    protected String clientSecret;

    @Property(name = "micronaut.application.base-ui-url")
    protected String baseUiUrl;

    private final DiscordApiClient discordApiClient;

    public AuthController(DiscordApiClient discordApiClient) {
        this.discordApiClient = discordApiClient;
    }

    /**
     * Endpoint for discord OAuth flow.
     *
     * @param clientCode Client authorization code.
     * @return an access token from discord.
     */
    @Post("/discord")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<DiscordAccessToken> discord(
            final String clientCode
    ) {
        final DiscordAccessToken token = discordApiClient.getAccessToken(
                clientId,
                clientSecret,
                "authorization_code",
                clientCode,
                baseUiUrl
        ).blockFirst();

        return HttpResponse.ok(token);
    }

}
