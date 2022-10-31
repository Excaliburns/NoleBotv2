package com.tut.nolebotv2core.apiconnect;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.GuildAuthStatus;
import com.tut.nolebotshared.entities.GuildRole;
import com.tut.nolebotshared.entities.GuildUser;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.exceptions.GuildNotFoundException;
import com.tut.nolebotshared.exceptions.NoleBotException;
import com.tut.nolebotshared.payloads.*;
import com.tut.nolebotv2core.util.permissions.GenericPermission;
import com.tut.nolebotv2core.util.permissions.PermissionCache;
import com.tut.nolebotv2core.util.settings.Settings;
import com.tut.nolebotv2core.util.settings.SettingsCache;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.tut.nolebotshared.enums.BroadcastType.*;

@AllArgsConstructor
public class ApiMessageHandler implements ApiWebSocketConnector.MessageHandler {
    private static final Logger logger = LogManager.getLogger(ApiMessageHandler.class);

    // todo: lang
    final String guildNotFound =
            "Guild [%s] could not be found. Make sure NoleBot is in the guild you are querying.";
    final String memberNotFound =
            "Member [%s] could not be found in Guild [%s]. Make sure they are in that guild.";

    private JDA jda;
    private ApiWebSocketConnector webSocketConnector;

    @Override
    public void handleMessage(BroadcastPackage message) {
        logger.debug(
                "Message - REQUEST: [BroadcastType: {}, CorrelationId: {}] ",
                message::getBroadcastType,
                message::getCorrelationId
        );

        final BroadcastPackage.BroadcastPackageBuilder broadcastPackageBuilder = BroadcastPackage.builder()
                .correlationId(message.getCorrelationId())
                .messageType(MessageType.RESPONSE);

        try {

            switch (message.getBroadcastType()) {
                case GET_FSU_USER: {
                    final MemberAndGuildPayload payload = (MemberAndGuildPayload) message.getPayload();
                    webSocketConnector.sendMessage(
                            broadcastPackageBuilder
                                    .payload(getMemberDetails(payload.memberId(), payload.guildId()))
                                    .broadcastType(GET_FSU_USER)
                                    .build()
                    );
                    break;
                }
                case GET_GUILD_USERS: {
                    final GetMembersPayload payload = (GetMembersPayload) message.getPayload();
                    sendUsers(payload.guildId(), payload.requesterUserId(), broadcastPackageBuilder, payload.search());
                    break;
                }
                case GET_GUILD_ROLES: {
                    final GetRolesPayload payload = (GetRolesPayload) message.getPayload();
                    sendRoles(payload.guildId(), payload.requesterUserId(), broadcastPackageBuilder);
                    break;
                }
                case GET_GUILD_AUTH_STATUSES: {
                    AuthStatusesPayload payload = (AuthStatusesPayload) message.getPayload();
                    sendGuildAuthStatuses(payload.userId(), broadcastPackageBuilder);
                    break;
                }
                case ASSIGN_ROLES: {
                    final AssignRolePayload payload = (AssignRolePayload) message.getPayload();
                    assignRoles(payload.roleIds(), payload.userIds(), payload.guildId());
                    break;
                }
                case ACK: {
                    webSocketConnector.sendMessage(
                            broadcastPackageBuilder
                                    .payload(" ")
                                    .correlationId(UUID.randomUUID())
                                    .broadcastType(ACK)
                                    .build()
                    );
                    break;
                }
                case HEARTBEAT: {
                    logger.debug("Received heartbeat {}", message::getCorrelationId);
                    break;
                }
                default: {
                    logger.warn(
                            "Message broadcast type {} did not have matching case",
                            message::getBroadcastType
                    );
                }
            }
        }
        catch (Exception e) {
            logger.error("WS request of type {} errored with message {}", message::getBroadcastType, e::getMessage);
            webSocketConnector.sendMessage(
                    broadcastPackageBuilder
                            .broadcastType(EXCEPTION)
                            .payload(e)
                            .build()
            );
        }
    }

    /**
     * Get details of a member of a guild.
     *
     * @param memberSnowflakeId member's snowflake ID
     * @param guildSnowflakeId  guild's snowflake ID
     * @return GuildUser
     */
    private GuildUser getMemberDetails(
            final String memberSnowflakeId,
            final String guildSnowflakeId
    ) throws GuildNotFoundException, ErrorResponseException, ExecutionException {

        final Guild guild = Optional.ofNullable(jda.getGuildById(guildSnowflakeId))
                .orElseThrow(() -> new GuildNotFoundException(
                        String.format(guildNotFound, guildSnowflakeId)
                ));
        //This SHOULD throw an error if member doesn't exist or isnt a member of the guild
        Member member = guild.retrieveMemberById(memberSnowflakeId).complete();

        Settings settings = SettingsCache.settingsCache.get(guildSnowflakeId);

        return new GuildUser(
                member.getId(),
                member.getEffectiveName(),
                member.getUser().getAsTag(),
                member.getRoles().stream().map(
                        role -> new GuildRole(
                                role.getId(),
                                role.getName(),
                                role.getColorRaw(),
                                null,
                                role.isPublicRole()
                        )
                ).collect(Collectors.toList()),
                member.getEffectiveAvatarUrl(),
                member.getRoles().stream().anyMatch((role -> {
                    return role.getId().equals(settings.getAdminRole());
                })),
                member.getRoles().stream().anyMatch((role -> {
                    return role.getId().equals(settings.getGameManagerRole());
                }))
        );
    }

    private void assignRoles(List<String> roleIds, List<String> userIds, String guildId) {
        Guild g = jda.getGuildById(guildId);
        userIds.forEach(u -> {
            roleIds.forEach(r -> {
                g.addRoleToMember(u, g.getRoleById(r)).queue();
            });
        });

    }

    private void sendUsers(
            String guildSnowflakeId,
            String requesterSnowflakeId,
            BroadcastPackage.BroadcastPackageBuilder broadcastPackageBuilder,
            String search
    ) throws GuildNotFoundException, NoleBotException, ExecutionException {
        final Guild guild = Optional.ofNullable(jda.getGuildById(guildSnowflakeId))
                .orElseThrow(() -> new GuildNotFoundException(
                        String.format(guildNotFound, guildSnowflakeId)
                ));
        guild.retrieveMembersByPrefix(search, 100).onSuccess((m) -> {
            List<GuildUser> users = m.stream().map((u) -> new GuildUser(
                    u.getId(),
                    u.getEffectiveName(),
                    null,
                    null,
                    u.getEffectiveAvatarUrl(),
                    false,
                    false
            )).toList();
            webSocketConnector.sendMessage(
                    broadcastPackageBuilder
                            .payload(new MembersPayload(new ArrayList<>(users)))
                            .broadcastType(GET_GUILD_USERS)
                            .build()
            );
        });
    }

    private void sendRoles(
            String guildSnowflakeId,
            String requesterSnowflakeId,
            BroadcastPackage.BroadcastPackageBuilder broadcastPackageBuilder
    ) throws GuildNotFoundException, NoleBotException, ExecutionException {
        final Guild guild = Optional.ofNullable(jda.getGuildById(guildSnowflakeId))
                .orElseThrow(() -> new GuildNotFoundException(
                        String.format(guildNotFound, guildSnowflakeId)
                ));

        List<GuildRole> roles = guild.getRoles().stream().map((jdaRole) -> {
            RoleIcon icon = jdaRole.getIcon();
            String iconLink = icon != null ? icon.getIconUrl() : null;

            return new GuildRole(
                    jdaRole.getId(),
                    jdaRole.getName(),
                    null,
                    iconLink,
                    jdaRole.isPublicRole());
        }).toList();
        webSocketConnector.sendMessage(
                broadcastPackageBuilder
                        .payload(new RolesPayload(new ArrayList<>(roles)))
                        .broadcastType(GET_GUILD_ROLES)
                        .build()
        );
    }
    private void sendGuildAuthStatuses(String userId,
                                       BroadcastPackage.BroadcastPackageBuilder builder
    ) throws RuntimeException {
        User requestingUser = jda.getUserById(userId);
        List<Guild> mutualGuilds = jda.getMutualGuilds(requestingUser);
        List<GuildAuthStatus> authStatuses = new ArrayList<>();

        mutualGuilds.forEach((guild -> {
            String guildId = guild.getId();
            Settings s = null;
            try {
                s = SettingsCache.settingsCache.get(guildId);
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            authStatuses.add(new GuildAuthStatus(
                guildId,
                s.isUserAdmin(userId),
                s.isUserGameManager(userId)
            ));
        }));
        webSocketConnector.sendMessage(
                builder.broadcastType(GET_GUILD_AUTH_STATUSES)
                        .messageType(MessageType.RESPONSE)
                        .payload(new AuthStatusesPayload(authStatuses, userId))
                        .build()
        );
    }
}
