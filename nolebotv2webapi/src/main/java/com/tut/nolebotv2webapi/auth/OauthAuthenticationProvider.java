package com.tut.nolebotv2webapi.auth;

import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Slf4j
public class OauthAuthenticationProvider implements AuthenticationProvider {
    @Property(name = "micronaut.security.oauth2.clients.discord.client-id")
    protected String clientId;

    @Property(name = "micronaut.security.oauth2.clients.discord.client-secret")
    protected String clientSecret;

    @Property(name = "micronaut.application.base-ui-url")
    protected String baseUiUrl;

    @Inject
    protected DiscordApiClient discordApiClient;

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.create(emitter -> {
            try {
                DiscordAccessToken token = discordApiClient.getAccessToken(
                        clientId,
                        clientSecret,
                        "authorization_code",
                        authenticationRequest.getSecret().toString(),
                        baseUiUrl
                ).blockFirst();
                DiscordUser user = discordApiClient.getDiscordUser(
                        "Bearer " + token.getAccess_token()
                ).blockFirst();
                HashMap<String, Object> claims = new HashMap<>();
                claims.put("discord_access_token", token.getAccess_token());
                claims.put("discord_username", user.username());
                emitter.success(AuthenticationResponse.success(user.id(), claims));
            }
            catch (Exception e) {
                emitter.error(AuthenticationResponse.exception()); //NOPMD
            }

        });
    }
}
