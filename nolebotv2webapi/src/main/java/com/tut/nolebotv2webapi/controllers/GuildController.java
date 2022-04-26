package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.payloads.GetMembersPayload;
import com.tut.nolebotshared.payloads.MemberAndGuildPayload;
import com.tut.nolebotshared.payloads.MembersPayload;
import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.GuildUser;
import com.tut.nolebotshared.enums.BroadcastType;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller("/guilds")
public class GuildController {
    private static final Logger logger = LogManager.getLogger(GuildController.class);

    @Inject
    private CoreWebSocketServer websocketServer;

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
