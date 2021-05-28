package com.tut.nolebotv2webapi.client;

import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;

@Header(name = "User-Agent", value = "Micronaut")
@Client("https://discord.com/api/")
public interface DiscordApiClient {

    @Get("oauth2/authorize")
    Flowable<DiscordAccessToken> getAccessToken(
            @Body String client_id,
            @Body String client_secret,
            @Body String grant_type,
            @Body String code,
            @Body String redirect_uri
    );
}
