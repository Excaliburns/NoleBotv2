package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import shared.entities.BroadcastPackage;
import shared.entities.GuildUser;
import shared.enums.BroadcastType;

import javax.inject.Inject;

@Controller("/guild")
public class GuildController {
    @Inject
    private CoreWebSocketServer websocketServer;

    @Post("/fsu/{userId}")
    public HttpResponse<GuildUser> getFsuUser(
            @PathVariable final String userId
    ) {
        try {
            BroadcastPackage broadcastPackage = websocketServer.sendWithResponse(
                    BroadcastPackage.builder().broadcastType(BroadcastType.GET_FSU_USER).payload(userId).build()
            );

            final GuildUser guildUser = (GuildUser) broadcastPackage.getPayload();

            return HttpResponse.ok(guildUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return HttpResponse.serverError();
    }
}
