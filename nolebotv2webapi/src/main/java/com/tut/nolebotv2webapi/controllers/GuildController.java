package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.Guild;
import com.tut.nolebotshared.entities.GuildUser;
import com.tut.nolebotshared.enums.BroadcastType;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.payloads.GetMembersPayload;
import com.tut.nolebotshared.exceptions.MemberNotFoundException;
import com.tut.nolebotshared.payloads.MemberAndGuildPayload;
import com.tut.nolebotshared.payloads.MembersPayload;
import com.tut.nolebotv2webapi.client.DiscordApiClient;
import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller("/guilds")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class GuildController {
    private static final Logger logger = LogManager.getLogger(GuildController.class);

    @Inject
    private DiscordApiClient discordApiClient;

    @Inject
    private CoreWebSocketServer websocketServer;

    /**
     * Gets a guild user from core and returns it to the client.
     *
     * @param authentication The authentication of the user, to get Discord Token
     * @return A List of Guilds the authenticated user is in
     */
    @SneakyThrows
    @Get
    public HttpResponse<List<Guild>> getUserGuilds(
            @NonNull Authentication authentication
    ) {
        final List<Guild> guilds = discordApiClient.getDiscordUserGuilds(
                "Bearer " + authentication.getAttributes().get("discord_access_token").toString()
        ).blockFirst();

        return HttpResponse.ok(guilds);
    }

    @Post("{guildId}/me")
    public HttpResponse<GuildUser> getCurrentUserGuildUser(
            @PathVariable final String guildId,
            @NonNull Authentication authentication
    ) {
        final String userId = authentication.getName();
        return getFsuUser(guildId, userId);
    }

    /**
     * Gets a guild user from core and returns it to the client.
     *
     * @param guildId GuildId to search
     * @param userId UserId to search.
     * @return A GuildUser, if found.
     */
    @Post("/{guildId}/{userId}")
    public HttpResponse<GuildUser> getFsuUser(
            @PathVariable final String guildId,
            @PathVariable final String userId
    ) {
        try {
            BroadcastPackage broadcastPackage = websocketServer.sendWithResponse(
                    BroadcastPackage.builder()
                                    .messageType(MessageType.REQUEST)
                                    .broadcastType(BroadcastType.GET_FSU_USER)
                                    .payload(new MemberAndGuildPayload(guildId, userId)).build()
            );
            if (broadcastPackage.getBroadcastType() == BroadcastType.EXCEPTION) {
                throw (Exception) broadcastPackage.getPayload();
            }
            final GuildUser guildUser = (GuildUser) broadcastPackage.getPayload();
            return HttpResponse.ok(guildUser);
        }
        catch (Exception e) {
            logger.error("Exception occurred getting FSU User: {}", e::getMessage);
        }

        return HttpResponse.serverError();
    }

    /**
     * Gets all users of a guild. Must be searched by name prefix.
     *
     * @param guildId GuildId to search
     * @param name Name prefix to search
     * @return A list of GuildUsers, if found.
     */
    @Get("/{guildId}/users/")
    public HttpResponse<List<GuildUser>> getAllUsers(@PathVariable String guildId, @QueryValue String name) {
        try {
            BroadcastPackage broadcastPackage = websocketServer.sendWithResponse(
                    BroadcastPackage.builder()
                            .messageType(MessageType.REQUEST)
                            .broadcastType(BroadcastType.GET_GUILD_USERS)
                            .payload(new GetMembersPayload(guildId, name)).build()
            );
            if (broadcastPackage.getBroadcastType() == BroadcastType.EXCEPTION) {
                throw (Exception) broadcastPackage.getPayload();
            }
            ArrayList<GuildUser> users = ((MembersPayload) broadcastPackage.getPayload()).users();
            return HttpResponse.ok(users);
        }
        catch (Exception e) {
            logger.error(
                    "Exception occurred getting all users from guild {} with search {}: {}",
                    () -> guildId,
                    () -> name,
                    e::getMessage
            );
        }

        return HttpResponse.serverError();
    }
}
