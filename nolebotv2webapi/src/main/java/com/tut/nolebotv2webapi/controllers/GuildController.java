package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.GuildRole;
import com.tut.nolebotshared.entities.GuildUser;
import com.tut.nolebotshared.entities.Role;
import com.tut.nolebotshared.enums.BroadcastType;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.payloads.GetMembersPayload;
import com.tut.nolebotshared.payloads.GetRolesPayload;
import com.tut.nolebotshared.payloads.MemberAndGuildPayload;
import com.tut.nolebotshared.payloads.MembersPayload;
import com.tut.nolebotshared.payloads.RolesPayload;
import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import com.tut.nolebotv2webapi.db.rolecategories.CategoryRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

// We should use this controller to get information about guilds, or user information that is guild specific
@Slf4j
@Controller("/guilds")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class GuildController {
    private static final Logger logger = LogManager.getLogger(GuildController.class);

    @Inject
    private CoreWebSocketServer websocketServer;

    @Inject
    private CategoryRepository categoryRepository;

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
    @Get("/{guildId}/{userId}")
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
        catch (TimeoutException e) {
            logger.error("Timed out while waiting for WebSocket response");
        }
        catch (Exception e) {
            logger.error("Error received from core {}", e::toString);
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
    public HttpResponse<List<GuildUser>> getAllUsers(Authentication authentication,
                                                     @PathVariable String guildId,
                                                     @QueryValue String name) {
        String effectiveName = name.isEmpty() ? "a" : name;
        try {
            BroadcastPackage broadcastPackage = websocketServer.sendWithResponse(
                    BroadcastPackage.builder()
                            .messageType(MessageType.REQUEST)
                            .broadcastType(BroadcastType.GET_GUILD_USERS)
                            .payload(new GetMembersPayload(guildId, authentication.getName(), effectiveName)).build()
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

    /**
     * Gets the list of roles the authed user is able to assign.
     *
     * @param authentication The authentication of the logged in user
     * @return A list of roles
     */
    @Get("/{guildId}/assignable_roles/")
    public HttpResponse<List<GuildRole>> getAssignableRoles(
            Authentication authentication,
            @PathVariable String guildId
    ) {
        String discordUserId = authentication.getName();
        Set<Role> roles = categoryRepository.findRolesByGuildIdAndOwnersOwnerId(guildId, discordUserId);
        List<GuildRole> guildRoles = new ArrayList<>();
        roles.forEach(role -> {
            guildRoles.add(new GuildRole(role.getRoleId(), role.getRoleName(), null, null, null));
        });
        return HttpResponse.ok().body(guildRoles);
    }

    /**
     * Gets all roles in a guild.
     *
     * @param authentication The Authentication of the current user
     * @param guildId The guild ID to get roles from
     * @return All roles in a guild
     * @throws ExecutionException If the web socket breaks
     * @throws InterruptedException If the web socket breaks
     * @throws TimeoutException If the web socket timesout
     */
    @Get("/{guildId}/all_roles/")
    public HttpResponse<List<GuildRole>> getAllRoles(Authentication authentication,
                                                     @PathVariable String guildId
    ) throws ExecutionException, InterruptedException, TimeoutException {
        String userId = authentication.getName();
        BroadcastPackage returnPackage = websocketServer.sendWithResponse(BroadcastPackage.builder()
                .broadcastType(BroadcastType.GET_GUILD_ROLES)
                .payload(new GetRolesPayload(guildId, userId))
                .messageType(MessageType.REQUEST)
                .build());
        return HttpResponse.ok(((RolesPayload) returnPackage.getPayload()).roles());
    }
}
