// CHECKSTYLE:OFF
package com.tut.nolebotv2webapi.client;

import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotshared.entities.Guild;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers("User-Agent: Micronaut")
public interface DiscordApiClient {

    @RequestLine("POST /oauth2/token")
    @Body("client_id={client_id}&client_secret={client_secret}" +
            "&grant_type={grant_type}&code={code}&redirect_uri={redirect_uri}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    DiscordAccessToken getAccessToken(
            @Param String client_id,
            @Param String client_secret,
            @Param String grant_type,
            @Param String code,
            @Param String redirect_uri
    );
    @RequestLine("GET /users/@me")
    @Headers("Authorization: Bearer {authToken}")
    DiscordUser getDiscordUser(
            @Param String authToken
    );

    @RequestLine("GET /users/@me/guilds")
    @Headers("Authorization: Bearer {authToken}")
    List<Guild> getDiscordUserGuilds(
            @Param String authToken
    );
}
