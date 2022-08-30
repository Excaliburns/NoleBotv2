package com.tut.nolebotv2core.listeners;

import com.tut.nolebotv2core.util.settings.Settings;
import com.tut.nolebotv2core.util.settings.SettingsFactory;
import com.tut.nolebotv2core.util.settings.SettingsManager;
import com.tut.nolebotv2core.util.social.SocialMediaEnum;
import com.tut.nolebotv2core.util.social.SocialMediaEvent;
import com.tut.nolebotv2core.util.social.SocialMediaManager;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AllArgsConstructor
public class SocialMediaEventListener extends ListenerAdapter {
    private SocialMediaManager socialMediaManager;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        final Settings guildSettings = SettingsFactory.getSettingsForGuildFromFile(event.getGuild().getId());
        final String guildSocialMediaChannel = guildSettings.getSocialMediaChannel();
        final SocialMediaEvent socialMediaEvent = new SocialMediaEvent(event);
        if (event.getChannel().getId().equals(guildSocialMediaChannel)) {
            socialMediaManager.sendPostToAllConnectors(socialMediaEvent);
        }

    }
}
