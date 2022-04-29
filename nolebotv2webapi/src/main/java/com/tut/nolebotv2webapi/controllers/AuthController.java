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
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import jakarta.inject.Inject;

import java.util.Date;

@Controller
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
     * @return signed JWT
     */
    @Post("oauth/discord")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<String> discord(
            final String clientCode
    ) throws JOSEException {
        final DiscordAccessToken token = discordApiClient.getAccessToken(
                clientId,
                clientSecret,
                "authorization_code",
                clientCode,
                baseUiUrl
        ).blockFirst();
        if (token == null) {
            return HttpResponse.badRequest();
        }
        final String jwt = authWithDiscord(token);

        return HttpResponse.ok(jwt);
    }

    private String authWithDiscord(final DiscordAccessToken authToken) throws JOSEException {
        final DiscordUser user = discordApiClient.getDiscordUser("Bearer " + authToken.getAccess_token()).blockFirst();
        if (user == null) {
            throw new UnsupportedOperationException(
                    String.format("Discord user was invalid. Auth token: %s", authToken)
            );
        }

        final SignedJWT jwt = generatorConfiguration.sign(
                new JWTClaimsSet.Builder()
                        .expirationTime(new Date(System.currentTimeMillis() + authToken.getExpires_in()))
                        .issueTime(new Date(System.currentTimeMillis()))
                        .subject(user.id())
                        .claim("auth_token", authToken.getAccess_token())
                        .build()
        );
        return jwt.serialize();
    }
}
