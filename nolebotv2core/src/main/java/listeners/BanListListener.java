package listeners;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import util.chat.EmbedHelper;
import util.settings.SettingsCache;

import java.util.concurrent.ExecutionException;
//Checks whether a member is shadow-banned when members try to join a voice channel or send a message
public class BanListListener extends ListenerAdapter {
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        try {
            if (SettingsCache.settingsCache.get(event.getGuild().getId()).getBannedUserIds().contains(event.getMember().getId())) {
                event.getGuild().kickVoiceMember(event.getMember()).queue(callback ->
                        event.getEntity().getUser().openPrivateChannel().queue(messageChannelCallback ->
                        messageChannelCallback.sendMessage("You have been shadow-banned from the server, so you were disconnected when you attempted to join a voice channel.\nContact an officer if this is not in order.").queue()));
            }
        } catch (ExecutionException e) {
            final MessageEmbed embed = EmbedHelper.getDefaultExceptionReactionMessage(e);

            if (event.getGuild().getSystemChannel() != null) {
                event.getGuild().getSystemChannel().sendMessage(embed).queue();
            }
            else {
                final MessageChannel channel = event.getGuild().getDefaultChannel();
                if (channel != null) {
                    channel.sendMessage("Your guild didn't have a system channel to send this error message, please set one up!").queue( callback ->
                            callback.getChannel().sendMessage(embed).queue()
                    );
                }
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        try {
            if (SettingsCache.settingsCache.get(event.getGuild().getId()).getBannedUserIds().contains(event.getAuthor().getId())) {
                if (event.getMember() != null) {
                    event.getChannel().deleteMessageById(event.getMessageId()).queue(callback ->
                            event.getAuthor().openPrivateChannel().queue(messageChannelCallback ->
                                    messageChannelCallback.sendMessage("You have been shadow-banned from the server, so your message was deleted.\nContact an officer if this is not in order.").queue()));
                }
            }
        } catch (ExecutionException e) {
            final MessageEmbed embed = EmbedHelper.getDefaultExceptionReactionMessage(e);

            if (event.getGuild().getSystemChannel() != null) {
                event.getGuild().getSystemChannel().sendMessage(embed).queue();
            }
            else {
                final MessageChannel channel = event.getGuild().getDefaultChannel();
                if (channel != null) {
                    channel.sendMessage("Your guild didn't have a system channel to send this error message, please set one up!").queue( callback ->
                            callback.getChannel().sendMessage(embed).queue()
                    );
                }
            }
        }
    }
}
