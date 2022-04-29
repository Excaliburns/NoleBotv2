// CHECKSTYLE:OFF
package com.tut.nolebotv2webapi.client;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotshared.entities.Guild;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.MediaType;
import reactor.core.publisher.Flux;

@Header(name = "User-Agent", value = "Micronaut")
@Client("https://discord.com/api/")
public interface DiscordApiClient {

    @Post("oauth2/token")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    Flux<DiscordAccessToken> getAccessToken(
            String client_id,
            String client_secret,
            String grant_type,
            String code,
            String redirect_uri
    );

    @Get("users/@me")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    Flux<DiscordUser> getDiscordUser(
            @Header(HttpHeaders.AUTHORIZATION) String authToken
    );

    @Get("users/@me/guilds")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    Flux<List<Guild>> getDiscordUserGuilds(
            @Header(HttpHeaders.AUTHORIZATION) String authToken
    );
}
