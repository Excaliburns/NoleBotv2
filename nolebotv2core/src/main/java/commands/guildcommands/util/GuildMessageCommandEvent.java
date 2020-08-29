package commands.guildcommands.util;

import commands.util.CommandEvent;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

@Getter @Setter
public class GuildMessageCommandEvent extends CommandEvent {
    private final GuildMessageReceivedEvent event;

    public GuildMessageCommandEvent(GuildMessageReceivedEvent event, List<String> message) {
        this.event = event;
        this.setMessageContent(message);
        this.setChannel(event.getChannel());
        this.setGuild(event.getGuild());
        this.setGuildId(event.getGuild().getId());
    }
}
