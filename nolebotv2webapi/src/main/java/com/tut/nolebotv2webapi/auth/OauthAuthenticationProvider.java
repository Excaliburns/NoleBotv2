package com.tut.nolebotv2webapi.auth;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotshared.entities.GuildAuthStatus;
import com.tut.nolebotshared.enums.BroadcastType;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.payloads.AuthStatusesPayload;
import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

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

    @Inject
    protected CoreWebSocketServer coreWebSocketServer;

    @Override
    @Blocking
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
                AuthStatusesPayload payload = (AuthStatusesPayload) coreWebSocketServer.sendWithResponse(
                        BroadcastPackage.builder()
                                .broadcastType(BroadcastType.GET_GUILD_AUTH_STATUSES)
                                .messageType(MessageType.REQUEST)
                                .payload(new AuthStatusesPayload(null, user.id()))
                                .build()
                ).getPayload();
                List<GuildAuthStatus> authStatuses = payload.authStatuses();

                HashMap<String, Object> claims = new HashMap<>();
                claims.put("discord_access_token", token.getAccess_token());
                claims.put("discord_username", user.username());
                claims.put("auth_statuses", authStatuses);
                emitter.success(AuthenticationResponse.success(user.id(), claims));
            }
            catch (Exception e) {
                log.error("{}", e::getMessage);
                emitter.error(AuthenticationResponse.exception()); //NOPMD
            }

        });
    }
}
