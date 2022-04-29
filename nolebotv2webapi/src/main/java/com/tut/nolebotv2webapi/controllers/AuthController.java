package com.tut.nolebotv2webapi.controllers;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import reactor.util.annotation.Nullable;

@Controller("/oauth")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class AuthController {
    @Get("/token")
    public HttpResponse<String> getAccessToken(@Nullable Authentication authentication) {
        return HttpResponse.ok().body(authentication.getAttributes().get("discord_access_token").toString());
    }
}
