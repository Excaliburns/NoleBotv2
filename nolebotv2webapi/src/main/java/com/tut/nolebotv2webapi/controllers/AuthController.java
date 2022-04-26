package com.tut.nolebotv2webapi.controllers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import jakarta.inject.Inject;

@Controller()
@Secured(SecurityRule.IS_ANONYMOUS)
public class AuthController {
    @Property(name = "micronaut.security.oauth2.clients.discord.client-id")
    protected String clientId;

    @Property(name = "micronaut.security.oauth2.clients.discord.client-secret")
    protected String clientSecret;

    @Property(name = "micronaut.application.base-ui-url")
    protected String baseUiUrl;

    private final DiscordApiClient discordApiClient;

    @Inject
    private SecretSignature generatorConfiguration;
    public AuthController(DiscordApiClient discordApiClient) {
        this.discordApiClient = discordApiClient;
    }

    /**
     * Endpoint for discord OAuth flow.
     *
     * @param clientCode Client authorization code.
     * @return an access token from discord.
     */
    @Post("oauth/discord")
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

    @Post("login/discord")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<String> login(@QueryValue String token) throws JOSEException {
        DiscordUser user = discordApiClient.getDiscordUser("Bearer " + token).blockFirst();
        SignedJWT jwt = generatorConfiguration.sign(new JWTClaimsSet.Builder().claim("username", user.id()).build());
        return HttpResponse.ok().body(jwt.toString());
    }
}
