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
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller("/guilds")
public class GuildController {
    @Inject
    private CoreWebSocketServer websocketServer;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.serverError();
    }

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
            e.printStackTrace();
        }

        return HttpResponse.serverError();
    }
}
