package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.enums.BroadcastType;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.payloads.AssignRolePayload;
import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import com.tut.nolebotv2webapi.db.rolecategories.CategoryRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;

import java.util.List;

// This controller should contain endpoints that cause the bot to do something.
@Controller("/bot")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class BotController {
    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private CoreWebSocketServer webSocketServer;

    /**
     * Endpoint for assigning roles to users.
     *
     * @param authentication The authentication of the current user
     * @param requestBody The body of the request (Gets bound to AssignRolePayload since it contains all necessary data)
     * @return An HTTPResponse indicating success or failure
     *
     */
    @Post("/assign_roles")
    public HttpResponse<String> assignRolesToUsers(Authentication authentication, @Body AssignRolePayload requestBody) {
        String assignerId = authentication.getName();
        List<String> roleIds = requestBody.roleIds();
        String guildId = requestBody.guildId();
        List<String> allowedRoles = categoryRepository.findRoleIdsByOwnerIdAndGuildId(assignerId, guildId);
        for (final String r : roleIds) {
            if (!allowedRoles.contains(r)) {
                return HttpResponse.unauthorized().body("Unauthorized role assignment");
            }
        }
        webSocketServer.send(BroadcastPackage.builder()
                .broadcastType(BroadcastType.ASSIGN_ROLES)
                .payload(requestBody)
                .messageType(MessageType.REQUEST)
                .build());
        // Do stuff
        return HttpResponse.ok();
    }
}
