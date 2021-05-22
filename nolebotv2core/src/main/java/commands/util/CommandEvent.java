package commands.util;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import util.permissions.PermissionCache;
import util.settings.Settings;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@Getter @Setter
public class CommandEvent {
    private GuildMessageReceivedEvent originatingJDAEvent;
    private Command                   command;

    // Message content with no separation
    private String         rawMessageContent;

    // Message content, index is position after first space, separated by spaces + 1
    // !command arg arg
    //  0       1   2
    private List<String>   messageContent;
    private Guild          guild;
    private String         guildId;
    private MessageChannel channel;
    private Settings       settings;

    private int            userInitiatedPermissionLevel;

    public CommandEvent(GuildMessageReceivedEvent event, String rawMessage, List<String> message, Settings settings, Command command) {
        this.originatingJDAEvent          = event;
        this.command                      = command;
        this.rawMessageContent            = rawMessage;
        this.messageContent               = message;
        this.guild                        = event.getGuild();
        this.guildId                      = event.getGuild().getId();
        this.channel                      = event.getChannel();
        this.settings                     = settings;
        this.userInitiatedPermissionLevel = PermissionCache.getPermissionForUser(
                event.getAuthor().getId(),
                event.getGuild().getId()
        ).getPermissionLevel();
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

    public void sendSuccessResponseToOriginatingChannel(String ...successMessageContent) {
        MessageUtil.sendSuccessResponseToChannel(this.channel, Arrays.asList(successMessageContent));
    }

    public void sendSuccessResponseToOriginatingChannel(List<String> successMessageContent) {
        MessageUtil.sendSuccessResponseToChannel(this.channel, successMessageContent);
    }

    public void printStackTraceToChannelFromThrowable(MessageChannel channel, Throwable e) {
        MessageUtil.printStackTraceToChannelFromThrowable(channel, e);
    }

    public void sendMessageToOriginatingChannel(Queue<Message> message) {
        MessageUtil.sendMessageToChannel(message, this.channel);
    }

    public void sendMessageToOriginatingChannel(Message message) {
        MessageUtil.sendMessageToChannel(message, this.channel);
    }

    public void sendMessageToOriginatingChannel(String message) {
        MessageUtil.sendMessageToChannel(message, this.channel);
    }

    public void sendMessageToOriginatingChannel(MessageEmbed embed) {
        MessageUtil.sendMessageToChannel(embed, this.channel);
    }

    public List<User> getMentionedUsers() {
        return this.originatingJDAEvent.getMessage().getMentionedUsers();
    }

    public List<Role> getMentionedRoles() {
        return this.originatingJDAEvent.getMessage().getMentionedRoles();
    }
}
