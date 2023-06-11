package com.tut.nolebotv2core;

import com.tut.nolebotv2core.apiconnect.ApiMessageHandler;
import com.tut.nolebotv2core.apiconnect.ApiWebSocketConnector;
import com.tut.nolebotv2core.enums.PropEnum;
import com.tut.nolebotv2core.listeners.BanListListener;
import com.tut.nolebotv2core.listeners.GuildMessageCommandListener;
import com.tut.nolebotv2core.listeners.GuildMessageReactionListener;
import com.tut.nolebotv2core.listeners.OnReadyListener;
import com.tut.nolebotv2core.listeners.SocialMediaEventListener;
import com.tut.nolebotv2core.util.NoleBotUtil;
import com.tut.nolebotv2core.util.PropertiesUtil;
import com.tut.nolebotv2core.util.db.DBConnection;
import com.tut.nolebotv2core.util.social.SocialMediaManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoleBot {

    private static final Logger logger = LogManager.getLogger(NoleBot.class);

    private static final OnReadyListener onReadyListener = new OnReadyListener();
    private static final GuildMessageCommandListener guildMessageCommandListener = new GuildMessageCommandListener();
    private static final GuildMessageReactionListener guildMessageReactionListener = new GuildMessageReactionListener();
    private static final BanListListener banListListener = new BanListListener();

    private static SocialMediaEventListener socialMediaEventListener;
    private static SocialMediaManager socialMediaManager;

    private static Connection dbconnection;

    /**
     * Entrypoint.
     *
     * @param args args passed to program by environment
     */
    public static void main(String[] args) {
        // Enable specific events from discord gateway.
        final List<GatewayIntent> INTENT_LIST = List.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
        );

        //Bot token from properties file
        String token = PropertiesUtil.getProperty(PropEnum.TOKEN);

        // For Heroku Deployment
        if (token == null) {
            token = System.getenv("TOKEN");
            if (token != null) {
                logger.info("Running in Heroku mode - changes will not persist through bot restarts.");
            }
        }

        // If it's still null, user probably hasn't set it.
        final JDA jda = JDABuilder.create(token, INTENT_LIST)
                // Add Listeners to JDA instance
                .addEventListeners(
                        onReadyListener,
                        guildMessageCommandListener,
                        guildMessageReactionListener,
                        banListListener
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        NoleBotUtil.setJda(jda);

        // TODO: Support multiple dbs? DBConnection should store variations of initializing them.
        // maybe some property like, switch (property) case mysql: initMysql, case postgreSql: initPostgre
        DBConnection.initDbConnection();
        dbconnection = DBConnection.getConnection();

        if (dbconnection == null) {
            logger.warn("Database connection couldn't be established.");
        }

        final boolean WEBSOCKET_ENABLED = Boolean.parseBoolean(
                PropertiesUtil.getProperty(PropEnum.API_WEBSOCKET_ENABLED)
        );
        logger.info("Websocket enabled? {}", WEBSOCKET_ENABLED);
        if (WEBSOCKET_ENABLED) {
            try {
                final ApiWebSocketConnector connector = ApiWebSocketConnector.tryConnectApi().get();
                connector.addMessageHandler(new ApiMessageHandler(jda, connector));
                NoleBotUtil.setApiWebSocketConnector(connector);
            }
            catch (InterruptedException | ExecutionException e) {
                logger.error("Fatal exception when connecting to API: {} ", e::getMessage);
            }
        }
        if (Boolean.parseBoolean(PropertiesUtil.getProperty(PropEnum.ENABLE_SOCIAL_MEDIA_INTEGRATION))) {
            socialMediaManager = new SocialMediaManager();
            socialMediaManager.createConnectors();

            socialMediaEventListener = new SocialMediaEventListener(socialMediaManager);
            jda.addEventListener(socialMediaEventListener);
        }
    }



}
