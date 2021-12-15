package apiconnect;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.entities.GuildRole;
import com.tut.nolebotshared.entities.GuildUser;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotshared.exceptions.GuildNotFoundException;
import com.tut.nolebotshared.exceptions.MemberNotFoundException;
import com.tut.nolebotshared.payloads.MemberAndGuildPayload;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.tut.nolebotshared.enums.BroadcastType.EXCEPTION;
import static com.tut.nolebotshared.enums.BroadcastType.GET_FSU_USER;

@AllArgsConstructor
public class ApiMessageHandler implements ApiWebSocketConnector.MessageHandler {
    private JDA jda;
    private ApiWebSocketConnector webSocketConnector;

    @Override
    public void handleMessage(BroadcastPackage message) {
        final BroadcastPackage.BroadcastPackageBuilder broadcastPackageBuilder = BroadcastPackage.builder()
                .correlationId(message.getCorrelationId())
                .messageType(MessageType.RESPONSE);

        try {
            switch (message.getBroadcastType()) {
                case GET_FSU_USER -> {
                    final MemberAndGuildPayload payload = (MemberAndGuildPayload) message.getPayload();
                    webSocketConnector.sendMessage(
                            broadcastPackageBuilder
                                    .payload(getMemberDetails(payload.memberId(), payload.guildId()))
                                    .broadcastType(GET_FSU_USER)
                                    .build()
                    );
                }
            }
        }
        catch ( Exception e ) {
            webSocketConnector.sendMessage(
                    broadcastPackageBuilder
                            .broadcastType(EXCEPTION)
                            .payload(e)
                            .build()
            );
        }
    }

    /**
     * Get details of a member of a guild
     * @param memberSnowflakeId member's snowflake ID
     * @param guildSnowflakeId guild's snowflake Id
     * @return GuildUser
     */
    private GuildUser getMemberDetails (final String memberSnowflakeId, final String guildSnowflakeId) throws GuildNotFoundException, MemberNotFoundException {
        final Guild guild = Optional.ofNullable(jda.getGuildById(guildSnowflakeId))
                .orElseThrow( () -> new GuildNotFoundException(
                        String.format("Guild [%s] could not be found. Make sure NoleBot is in the guild you are querying.", guildSnowflakeId)
                ));
        final Member member = Optional.ofNullable(guild.getMemberById(memberSnowflakeId))
                .orElseThrow( () -> new MemberNotFoundException(
                        String.format("Member [%s] could not be found in Guild [%s]. Make sure they are in that guild.", memberSnowflakeId, guildSnowflakeId)
                ));

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
}
