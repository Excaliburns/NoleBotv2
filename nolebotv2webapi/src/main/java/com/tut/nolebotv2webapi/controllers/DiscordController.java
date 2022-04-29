package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotv2webapi.client.DiscordApiClient;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

@Controller("/discord")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class DiscordController {
    @Inject
    DiscordApiClient discordApiClient;

    /**
     * Gets user info from Discord API.
     *
     * @param authentication The authentication of the current user
     * @return A DiscordUser response from Discord
     */
    @Get("/user_info")
    public HttpResponse<DiscordUser> getUserInfo(Authentication authentication) {
        DiscordUser user = discordApiClient.getDiscordUser("Bearer " + authentication.getAttributes()
                .get("discord_access_token").toString()).blockFirst();
        return HttpResponse.ok(user);
    }
}
