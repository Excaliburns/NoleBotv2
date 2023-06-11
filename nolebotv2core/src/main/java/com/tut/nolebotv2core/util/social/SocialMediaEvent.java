package com.tut.nolebotv2core.util.social;

import com.tut.nolebotv2core.util.settings.Settings;
import com.tut.nolebotv2core.util.settings.SettingsFactory;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Getter
@Setter
public class SocialMediaEvent {
    private MessageReceivedEvent origEvent;
    private Message message;
    private Settings guildSettings;

    /**
     * An event representing a post to be posted to social media.
     *
     * @param event The message to convert into a post
     */
    public SocialMediaEvent(MessageReceivedEvent event) {
        origEvent = event;
        message = event.getMessage();
        guildSettings = SettingsFactory.getSettings(event.getGuild());
    }
}
