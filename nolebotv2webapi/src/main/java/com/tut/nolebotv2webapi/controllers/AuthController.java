package com.tut.nolebotv2webapi.controllers;


import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.entities.DiscordAccessToken;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import reactor.util.annotation.Nullable;

import java.security.Principal;

@Controller("/oauth")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class AuthController {
    @Get("/token")
    public HttpResponse<String> getAccessToken(@Nullable Authentication authentication) {
        return HttpResponse.ok().body(authentication.getAttributes().get("discord_access_token").toString());
    }
}
