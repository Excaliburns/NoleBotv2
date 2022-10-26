package com.tut.nolebotv2core.util.social;

import com.tut.nolebotv2core.util.NoleBotUtil;
import com.tut.nolebotv2core.util.settings.Settings;
import com.tut.nolebotv2core.util.settings.SettingsFactory;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class SocialMediaManager {
    private Map<String, List<SocialMediaConnector>> connectorMap;

    public SocialMediaManager() {
        connectorMap = new HashMap<>();
    }

    /**
     * Creates a post from the event triggered by a message in a specific channel.
     *
     * @param e The event that contains the social media post
     *
     */
    public void sendPostToAllConnectors(SocialMediaEvent e) {
        List<SocialMediaConnector> connectors = connectorMap.get(e.getOrigEvent().getGuild().getId());
        log.debug("Attempting to send ");
        connectors.forEach(c -> {
            c.post(e.getMessage());
        });
    }

    /**
     * Creates all social media connectors.
     */
    public void createConnectors() {
        final JDA jda = NoleBotUtil.getJda();
        jda.getGuilds().forEach(g -> {
            ArrayList<SocialMediaConnector> socials = new ArrayList<>();
            Settings s = SettingsFactory.getSettings(g);
            s.getEnabledSocials().forEach(social -> {
                if (social.equals(SocialMediaEnum.FACEBOOK)) {
                    //Not yet implemented
                }
                if (social.equals(SocialMediaEnum.TWITTER)) {
                    TwitterConnector connector = new TwitterConnector(
                            s.getTwitterApiKey(),
                            s.getTwitterApiSecret(),
                            s.getTwitterAccessToken(),
                            s.getTwitterAccessSecret());
                    socials.add(connector);
                }
                if (social.equals(SocialMediaEnum.INSTAGRAM)) {
                    // Not yet implemented
                }
            });
            connectorMap.put(g.getId(), socials);
        });
    }
}
