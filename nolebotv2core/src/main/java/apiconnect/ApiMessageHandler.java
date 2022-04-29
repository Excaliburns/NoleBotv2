package apiconnect;

import com.tut.nolebotshared.payloads.GetMembersPayload;
import com.tut.nolebotshared.payloads.MembersPayload;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.GuildRole;
import com.tut.nolebotshared.entities.GuildUser;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.exceptions.GuildNotFoundException;
import com.tut.nolebotshared.payloads.MemberAndGuildPayload;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tut.nolebotshared.enums.BroadcastType.EXCEPTION;
import static com.tut.nolebotshared.enums.BroadcastType.GET_FSU_USER;
import static com.tut.nolebotshared.enums.BroadcastType.GET_GUILD_USERS;

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
                    sendUsers(payload.guildId(), broadcastPackageBuilder, payload.search());
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
     * @param guildSnowflakeId  guild's snowflake Id
     * @return GuildUser
     */
    private GuildUser getMemberDetails(
            final String memberSnowflakeId,
            final String guildSnowflakeId
    ) throws GuildNotFoundException, ErrorResponseException {

        final Guild guild = Optional.ofNullable(jda.getGuildById(guildSnowflakeId))
                .orElseThrow(() -> new GuildNotFoundException(
                        String.format(guildNotFound, guildSnowflakeId)
                ));
        //This SHOULD throw an error if member doesn't exist or isnt a member of the guild
        Member member = guild.retrieveMemberById(memberSnowflakeId).complete();

        return new GuildUser(
                member.getId(),
                member.getEffectiveName(),
                member.getUser().getAsTag(),
                member.getRoles().stream().map(
                        role -> new GuildRole(
                                role.getId(),
                                role.getName(),
                                role.getColorRaw(), role.isPublicRole()
                        )
                ).collect(Collectors.toList()),
                member.getEffectiveAvatarUrl()
        );
    }

    private void sendUsers(
            String guildSnowflakeId,
            BroadcastPackage.BroadcastPackageBuilder broadcastPackageBuilder,
            String search
    ) throws GuildNotFoundException {
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
                    u.getEffectiveAvatarUrl()
            )).collect(Collectors.toList());
            webSocketConnector.sendMessage(
                    broadcastPackageBuilder
                            .payload(new MembersPayload(new ArrayList<>(users)))
                            .broadcastType(GET_GUILD_USERS)
                            .build()
            );
        });
        /*
        guild.loadMembers().onSuccess((t) -> {
            ArrayList<GuildUser> users = new ArrayList<>();
            int numPages = (int) Math.ceil(((double) t.size()) / 300.0);
            for (int i = 300 * (pageNum - 1); i < 300 * pageNum && i < t.size(); i++) {
                final Member member = t.get(i);
                final GuildUser user = new GuildUser(
                        member.getId(),
                        member.getEffectiveName(),
                        null,
                        null,
                        member.getEffectiveAvatarUrl()
                );
                users.add(user);
            }
            webSocketConnector.sendMessage(
                    broadcastPackageBuilder
                            .payload(new MembersPayload(users, numPages))
                            .broadcastType(GET_GUILD_USERS)
                            .build()
            );
        });

         */

    }
}
