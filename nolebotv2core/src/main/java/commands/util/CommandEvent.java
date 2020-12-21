package commands.util;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.settings.Settings;

import java.util.Arrays;
import java.util.List;

@Getter @Setter
public class CommandEvent {
    private GuildMessageReceivedEvent originatingJDAEvent;
    private Command                   command;

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
        MessageUtil.sendErrorResponseToChannel(errorMessageContent, this.channel);
    }

    public void sendErrorResponseToOriginatingChannel(String ...errorMessages) {
        MessageUtil.sendErrorResponseToChannel(this.channel, Arrays.asList(errorMessages));
    }

    public void sendErrorResponseToOriginatingChannel(List<String> errorMessageContent) {
        MessageUtil.sendErrorResponseToChannel(this.channel, errorMessageContent);
    }

    public void sendSuccessResponseToOriginatingChannel(String successMessageContent) {
        MessageUtil.sendSuccessResponseToChannel(successMessageContent, this.channel);
    }

    public void printStackTraceToChannelFromThrowable(MessageChannel channel, Throwable e) {
        MessageUtil.printStackTraceToChannelFromThrowable(channel, e);
    }

    public void sendMessageToOriginatingChannel(String message) {
        MessageUtil.sendMessageToChannel(message, this.channel);
    }

    public void sendMessageToOriginatingChannel(MessageEmbed embed) {
        MessageUtil.sendMessageToChannel(embed, this.channel);
    }
}
