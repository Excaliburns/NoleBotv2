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
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Log4j2
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
                HashMap<String, String> params = new HashMap<>();
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);
                params.put("grant_type", "authorization_code");
                params.put("code", authenticationRequest.getSecret().toString());
                params.put("redirect_uri", baseUiUrl);
                DiscordAccessToken token = discordApiClient.getAccessToken(
                        clientId,
                        clientSecret,
                        "authorization_code",
                        authenticationRequest.getSecret().toString(),
                        baseUiUrl
                );
                DiscordUser user = discordApiClient.getDiscordUser(
                        token.getAccess_token()
                );
                HashMap<String, Object> claims = new HashMap<>();
                claims.put("discord_access_token", token.getAccess_token());
                claims.put("discord_username", user.username());
                emitter.success(AuthenticationResponse.success(user.id(), claims));
            }
            catch (Exception e) {
                log.error("{}", e::getMessage);
                emitter.error(AuthenticationResponse.exception()); //NOPMD
            }

        });
    }
}
