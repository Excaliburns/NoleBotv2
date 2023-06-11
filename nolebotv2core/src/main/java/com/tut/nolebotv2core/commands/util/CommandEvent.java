package com.tut.nolebotv2core.commands.util;

import com.tut.nolebotv2core.util.permissions.PermissionCache;
import com.tut.nolebotv2core.util.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
public class CommandEvent {
    private SlashCommandInteractionEvent originatingJDAEvent;
    private Command                   command;
    private Guild          guild;
    private String         guildId;
    private MessageChannelUnion channel;
    private Settings       settings;

    private int            userInitiatedPermissionLevel;

    /**
     * Default constructor.
     *
     * @param event Originating JDA event
     * @param settings Settings of the guild that sent the command
     * @param command The command used.
     */
    public CommandEvent(
            final SlashCommandInteractionEvent event,
            final Settings settings,
            final Command command
    ) {
        this.originatingJDAEvent          = event;
        this.command                      = command;
        this.guild                        = event.getGuild();
        this.guildId                      = event.getGuild().getId();
        this.channel                      = event.getChannel();
        this.settings                     = settings;
        this.userInitiatedPermissionLevel = PermissionCache.getPermissionForUser(
                event.getUser().getId(),
                event.getGuild().getId()
        ).getPermissionLevel();
    }

    public void sendErrorResponseToOriginatingChannel(final String errorMessageContent) {
        MessageUtil.sendErrorResponseToChannel(errorMessageContent, this.channel);
    }

    public void sendErrorResponseToOriginatingChannel(final String ...errorMessages) {
        MessageUtil.sendErrorResponseToChannel(this.channel, Arrays.asList(errorMessages));
    }

    public void sendErrorResponseToOriginatingChannel(final List<String> errorMessageContent) {
        MessageUtil.sendErrorResponseToChannel(this.channel, errorMessageContent);
    }

    public void sendSuccessResponseToOriginatingChannel(final String successMessageContent) {
        MessageUtil.sendSuccessResponseToChannel(successMessageContent, this.channel);
    }

    public void sendSuccessResponseToOriginatingChannel(final String ...successMessageContent) {
        MessageUtil.sendSuccessResponseToChannel(this.channel, Arrays.asList(successMessageContent));
    }

    public void sendSuccessResponseToOriginatingChannel(final List<String> successMessageContent) {
        MessageUtil.sendSuccessResponseToChannel(this.channel, successMessageContent);
    }

    public void printStackTraceToChannelFromThrowable(final MessageChannelUnion channel, final Throwable e) {
        MessageUtil.printStackTraceToChannelFromThrowable(channel, e);
    }

    public void sendMessageToOriginatingChannel(final Queue<MessageCreateData> message) {
        MessageUtil.sendMessageToChannel(message, this.channel);
    }

    public void sendMessageToOriginatingChannel(final MessageCreateData message) {
        MessageUtil.sendMessageToChannel(message, this.channel);
    }

    public void sendMessageToOriginatingChannel(final String message) {
        MessageUtil.sendMessageToChannel(message, this.channel);
    }

    public void sendMessageToOriginatingChannel(final MessageEmbed embed) {
        MessageUtil.sendMessageToChannel(embed, this.channel);
    }

}
