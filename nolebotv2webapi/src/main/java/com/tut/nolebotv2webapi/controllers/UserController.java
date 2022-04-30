package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.entities.DiscordUser;
import com.tut.nolebotshared.entities.Guild;
import com.tut.nolebotv2webapi.client.DiscordApiClient;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import reactor.util.annotation.Nullable;

import java.util.List;

@Controller("/user")
@Secured({SecurityRule.IS_AUTHENTICATED})
public class UserController {
    //private static final Logger logger = LogManager.getLogger(GuildController.class);

    @Inject
    private DiscordApiClient discordApiClient;

    /**
     * Gets a list of guilds the authenticated user is in.
     *
     * @param authentication The authentication of the user, to get Discord Token
     * @return A List of Guilds the authenticated user is in
     */
    @SneakyThrows
    @Get("/guilds")
    public HttpResponse<List<Guild>> getUserGuilds(
            @NonNull Authentication authentication
    ) {
        final List<Guild> guilds = discordApiClient.getDiscordUserGuilds(
                "Bearer " + authentication.getAttributes().get("discord_access_token").toString()
        ).blockFirst();

        return HttpResponse.ok(guilds);
    }

    @Get("/token")
    public HttpResponse<String> getAccessToken(@Nullable Authentication authentication) {
        return HttpResponse.ok().body(authentication.getAttributes().get("discord_access_token").toString());
    }

    /**
     * Gets user info from Discord API.
     *
     * @param authentication The authentication of the current user
     * @return A DiscordUser response from Discord
     */
    @Get("/info")
    public HttpResponse<DiscordUser> getUserInfo(Authentication authentication) {
        DiscordUser user = discordApiClient.getDiscordUser("Bearer " + authentication.getAttributes()
                .get("discord_access_token").toString()).blockFirst();
        return HttpResponse.ok(user);
    }
}
