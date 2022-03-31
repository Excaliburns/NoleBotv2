import apiconnect.ApiMessageHandler;
import apiconnect.ApiWebSocketConnector;
import com.google.common.collect.Lists;
import commands.general.GetRoleID;
import commands.general.GetUserID;
import commands.general.Help;
import commands.guildcommands.Attendance;
import commands.guildcommands.HelloWorld;
import commands.guildcommands.ShadowBan;
import commands.guildcommands.guilds.SetPrefix;
import commands.guildcommands.guilds.permissions.ListGuildPermissions;
import commands.guildcommands.guilds.roles.AddRole;
import commands.guildcommands.guilds.roles.GivePerms;
import commands.guildcommands.guilds.roles.LockRole;
import commands.guildcommands.guilds.roles.UnlockRole;
import commands.util.CommandUtil;
import enums.PropEnum;
import listeners.BanListListener;
import listeners.GuildMessageCommandListener;
import listeners.GuildMessageReactionListener;
import listeners.OnReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.NoleBotUtil;
import util.PropertiesUtil;
import util.db.DBConnection;
import javax.security.auth.login.LoginException;
import java.net.URI;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NoleBot {

    private static final Logger logger = LogManager.getLogger(NoleBot.class);

    private static final CommandUtil commandUtil = new CommandUtil();
    private static final OnReadyListener onReadyListener = new OnReadyListener();
    private static final GuildMessageCommandListener guildMessageCommandListener = new GuildMessageCommandListener();
    private static final GuildMessageReactionListener guildMessageReactionListener = new GuildMessageReactionListener();
    private static final BanListListener banListListener = new BanListListener();

    private static Connection dbconnection;

    public static void main(String[] args) {
        // Enable specific events from discord gateway.
        final List<GatewayIntent> INTENT_LIST = List.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS
                );

        // Add Commands
        commandUtil.addCommand(new Help());
        commandUtil.addCommand(new HelloWorld());
        commandUtil.addCommand(new SetPrefix());
        commandUtil.addCommand(new ListGuildPermissions());
        commandUtil.addCommand(new Attendance());
        commandUtil.addCommand(new ShadowBan());
        commandUtil.addCommand(new AddRole());
        commandUtil.addCommand(new GetRoleID());
        commandUtil.addCommand(new GetUserID());
        commandUtil.addCommand(new LockRole());
        commandUtil.addCommand(new UnlockRole());
        commandUtil.addCommand(new GivePerms());

        try {
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
                            commandUtil,
                            onReadyListener,
                            guildMessageCommandListener,
                            guildMessageReactionListener,
                            banListListener
                    ).build();

            NoleBotUtil.setJda(jda);

            // TODO: Support multiple dbs? DBConnection should store variations of initializing them.
            // maybe some property like, switch (property) case mysql: initMysql, case postgreSql: initPostgre
            DBConnection.initMySqlConnection();
            dbconnection = DBConnection.getConnection();

            if (dbconnection == null) {
                logger.warn("Database connection couldn't be established.");
            }

            final boolean WEBSOCKET_ENABLED = Boolean.parseBoolean(
                    PropertiesUtil.getProperty(PropEnum.API_WEBSOCKET_ENABLED));
            logger.info("Websocket enabled? {}", WEBSOCKET_ENABLED);
            if (WEBSOCKET_ENABLED) {
                try {
                    final ApiWebSocketConnector connector = tryConnectApi().get();
                    connector.addMessageHandler(new ApiMessageHandler(jda, connector));
                    NoleBotUtil.setApiWebSocketConnector(connector);
                }
                catch (InterruptedException | ExecutionException e) {
                    logger.error("Fatal exception when connecting to API: {} ", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        catch (LoginException e) {
            logger.fatal("Could not initialize bot instance. Was token incorrect? {}", e.getMessage());
        }
    }

    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static CompletableFuture<ApiWebSocketConnector> tryConnectApi() {
        CompletableFuture<ApiWebSocketConnector> completableFuture = new CompletableFuture<>();
        final ScheduledFuture<?> checkFuture = executorService.scheduleAtFixedRate(() -> {
            try {
                final ApiWebSocketConnector connector = new ApiWebSocketConnector(URI.create(
                        "ws://localhost:8080/internalApi/" + PropertiesUtil.getProperty(
                                PropEnum.API_WEBSOCKET_SECRET)));
                completableFuture.complete(connector);
            }
            catch (Exception e) {
                logger.info("Swallowing exception, will attempt to reconnect api in 10 seconds.");
            }
        }, 0, 10, TimeUnit.SECONDS);
        completableFuture.whenComplete((result, thrown) -> checkFuture.cancel(true));
        return completableFuture;
    }


}
