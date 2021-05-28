package com.tut.nolebotv2webapi.mappers;

import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.oauth2.endpoint.authorization.state.State;
import io.micronaut.security.oauth2.endpoint.token.response.OauthUserDetailsMapper;
import io.micronaut.security.oauth2.endpoint.token.response.TokenResponse;
import jdk.jfr.Name;
import org.reactivestreams.Publisher;

import javax.inject.Named;
import javax.inject.Singleton;

@Named("discord")
@Singleton
public class DiscordAccessTokenMapper implements OauthUserDetailsMapper {
    private final DiscordApiClient discordApiClient;

    @Property(name = "")
    protected String client_id;

    DiscordAccessTokenMapper(DiscordApiClient discordApiClient) {
        this.discordApiClient = discordApiClient;
    }


    /**
     * Convert the token response into a user details.
     *
     * @param tokenResponse The token response
     * @return The user details
     * @deprecated Use {@link #createAuthenticationResponse(TokenResponse, State) instead}. This
     * method will only be called if the new method is not overridden.
     */
    @Override
    public Publisher<UserDetails> createUserDetails(TokenResponse tokenResponse) {
        return null;
    }

    /**
     * Convert the token response and state into an authentication response.
     *
     * @param tokenResponse The token response
     * @param state         The OAuth state
     * @return The authentication response
     */
    @Override
    public Publisher<AuthenticationResponse> createAuthenticationResponse(TokenResponse tokenResponse, State state) {
        return discordApiClient.getAccessToken(

        )
    }
}
