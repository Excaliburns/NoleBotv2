package commands.util;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.settings.Settings;

import java.util.List;

@Getter @Setter
public class CommandEvent {
    private Event          originatingJDAEvent;
    private Command        command;

    private List<String>   messageContent;
    private Guild          guild;
    private String         guildId;
    private MessageChannel channel;
    private Settings       settings;

    public CommandEvent(GuildMessageReceivedEvent event, List<String> message, Settings settings, Command command) {
        this.originatingJDAEvent = event;
        this.command             = command;
        this.messageContent      = message;
        this.guild               = event.getGuild();
        this.guildId             = event.getGuild().getId();
        this.channel             = event.getChannel();
        this.settings            = settings;
    }

    public void sendErrorResponseToOriginatingChannel(String errorMessageContent) {
        channel.sendMessage("\u203C     " + errorMessageContent + "     \u203C").queue();
    }

    public void sendSuccessResponseToOriginatingChannel(String successMessageContent) {
        channel.sendMessage("\u2705 \u2014 " + successMessageContent).queue();
    }
}
