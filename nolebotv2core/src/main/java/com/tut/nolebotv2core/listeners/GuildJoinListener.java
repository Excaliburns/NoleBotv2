package com.tut.nolebotv2core.listeners;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class GuildJoinListener extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger(GuildJoinListener.class);

    //Logs when bot joins a new guild
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        super.onGuildJoin(event);
        logger.info("Joined new guild {}", () -> event.getGuild().getName());
    }
}
