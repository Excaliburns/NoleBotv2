package com.tut.nolebotv2core.listeners;

import com.tut.nolebotv2core.util.chat.EmbedHelper;
import com.tut.nolebotv2core.util.settings.SettingsCache;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

//Checks whether a member is shadow-banned when members try to join a voice channel or send a message
public class BanListListener extends ListenerAdapter {
    //todo: lang
    final String shadowBanJoinVoiceText = "You have been shadow-banned from the server, so you were disconnected " +
                                          "when you attempted to join a voice channel." +
                                          "\nContact an officer if this is not in order.";

    final String shadowBanSendMessageText = "You have been shadow-banned from the server, so your message was" +
                                            " deleted.\nContact an officer if this is not in order.";

    /**
     * Kicks the shadow-banned user when the user joins a voice channel.
     *
     * @param event Originating JDA event
     */
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        final Guild guild = event.getGuild();
        try {
            if (SettingsCache.settingsCache.get(guild.getId()).getBannedUserIds().contains(event.getMember().getId())) {
                guild.kickVoiceMember(event.getMember()).queue(callback ->
                        event.getEntity().getUser().openPrivateChannel().queue(messageChannelCallback ->
                                messageChannelCallback.sendMessage(shadowBanJoinVoiceText).queue()
                        )
                );
            }
        }
        catch (ExecutionException e) {
            sendErrorMessage(e, guild);
        }
    }

    /**
     * Deletes messages sent by the user.
     *
     * @param event Originating JDA event
     */
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        final Guild guild = event.getGuild();
        try {
            if (SettingsCache.settingsCache.get(guild.getId()).getBannedUserIds().contains(event.getAuthor().getId())) {
                if (event.getMember() != null) {
                    event.getChannel().deleteMessageById(event.getMessageId()).queue(callback ->
                            event.getAuthor().openPrivateChannel().queue(messageChannelCallback ->
                                    messageChannelCallback.sendMessage(shadowBanSendMessageText).queue()
                            )
                    );
                }
            }
        }
        catch (ExecutionException e) {
            sendErrorMessage(e, guild);
        }
    }

    /**
     * Sends error message if sending the message to the shadow-banned user failed.
     *
     * @param e Exception that was thrown
     * @param guild Guild where the message was sent
     */
    private void sendErrorMessage(final Exception e, final Guild guild) {
        final MessageEmbed embed = EmbedHelper.getDefaultExceptionReactionMessage(e);

        if (guild.getSystemChannel() != null) {
            guild.getSystemChannel().sendMessageEmbeds(embed).queue();
        }
        else {
            final MessageChannel channel = guild.getDefaultChannel();
            if (channel != null) {
                channel.sendMessage(
                        "Your guild didn't have a system channel to send this error message, please set one up!"
                ).queue(callback ->
                        callback.getChannel().sendMessageEmbeds(embed).queue()
                );
            }
        }
    }
}
