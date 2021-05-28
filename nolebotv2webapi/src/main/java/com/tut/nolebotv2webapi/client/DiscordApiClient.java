package com.tut.nolebotv2webapi.client;

import com.tut.nolebotv2webapi.dtos.DiscordAccessTokenRequestDto;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.MediaType;
import io.reactivex.Flowable;

@Header(name = "User-Agent", value = "Micronaut")
@Client("https://discord.com/api/")
public interface DiscordApiClient {

    @Post("oauth2/token")
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    Flowable<DiscordAccessToken> getAccessToken(
            String client_id,
            String client_secret,
            String grant_type,
            String code,
            String redirect_uri
    );
}
