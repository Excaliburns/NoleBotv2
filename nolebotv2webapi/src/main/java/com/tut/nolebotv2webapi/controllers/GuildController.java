package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.payloads.MemberAndGuildPayload;
import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.GuildUser;
import com.tut.nolebotshared.enums.BroadcastType;

import javax.inject.Inject;

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

            final GuildUser guildUser = (GuildUser) broadcastPackage.getPayload();

            return HttpResponse.ok(guildUser);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.serverError();
    }
}
