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
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.tut.nolebotshared.enums.BroadcastType.EXCEPTION;
import static com.tut.nolebotshared.enums.BroadcastType.GET_FSU_USER;

@AllArgsConstructor
public class ApiMessageHandler implements ApiWebSocketConnector.MessageHandler {
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
                case GET_FSU_USER -> {
                    final MemberAndGuildPayload payload = (MemberAndGuildPayload) message.getPayload();
                    webSocketConnector.sendMessage(
                            broadcastPackageBuilder
                                    .payload(getMemberDetails(payload.memberId(), payload.guildId()))
                                    .broadcastType(GET_FSU_USER)
                                    .build()
                    );
                }
                default -> {
                 // no op
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
    ) throws GuildNotFoundException, ErrorResponseException{

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
}
