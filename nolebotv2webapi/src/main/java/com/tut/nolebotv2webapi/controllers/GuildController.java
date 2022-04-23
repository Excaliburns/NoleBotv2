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
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;

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
    public HttpResponse<GuildUser[]> getAllUsers(@PathVariable String guildId) {
        try {
            BroadcastPackage broadcastPackage = websocketServer.sendWithResponse(
                    BroadcastPackage.builder()
                            .messageType(MessageType.REQUEST)
                            .broadcastType(BroadcastType.GET_GUILD_USERS)
                            .payload(new GetMembersPayload(guildId, 1)).build()
            );
            if (broadcastPackage.getBroadcastType() == BroadcastType.EXCEPTION) {
                throw (Exception) broadcastPackage.getPayload();
            }
            int numPages = ((MembersPayload) broadcastPackage.getPayload()).numPages();
            ArrayList<GuildUser> users =((MembersPayload) broadcastPackage.getPayload()).users();
            for (int i = 2; i <= numPages; i++) {
                broadcastPackage = websocketServer.sendWithResponse(
                        BroadcastPackage.builder()
                                .messageType(MessageType.REQUEST)
                                .broadcastType(BroadcastType.GET_GUILD_USERS)
                                .payload(new GetMembersPayload(guildId, i)).build()
                );
                users.addAll(((MembersPayload) broadcastPackage.getPayload()).users());
            }
            GuildUser[] userArray = new GuildUser[users.size()];
            users.toArray(userArray);
            return HttpResponse.ok(userArray);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.serverError();
    }
}
