import commands.general.GetRoleID;
import commands.general.GetUserID;
import commands.general.Help;
import commands.guildcommands.Attendance;
import commands.guildcommands.HelloWorld;
import commands.guildcommands.ShadowBan;
import commands.guildcommands.guilds.AddRole;
import commands.guildcommands.guilds.SetPrefix;
import commands.guildcommands.guilds.permissions.ListGuildPermissions;
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
import util.PropertiesUtil;
import util.db.DBConnection;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.util.List;

public class NoleBot {
    private static final Logger logger = LogManager.getLogger(NoleBot.class);

    private static final CommandUtil commandUtil = new CommandUtil();
    private static final OnReadyListener onReadyListener = new OnReadyListener();
    private static final GuildMessageCommandListener guildMessageCommandListener = new GuildMessageCommandListener();
    private static final GuildMessageReactionListener guildMessageReactionListener = new GuildMessageReactionListener();
    private static final BanListListener              banListListener              = new BanListListener();

    private static Connection dbconnection;

    public static void main(String[] args) {
        // Enable specific events from discord gateway.
        final List<GatewayIntent> INTENT_LIST = List.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS);

        JDA jda;

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

        try {
            String token = PropertiesUtil.getProperty(PropEnum.TOKEN);

            // For Heroku Deployment
            if (token == null) {
                token = System.getenv("TOKEN");
                if (token != null) {
                    logger.info("Running in Heroku mode - changes will not persist through bot restarts.");
                }
            }

            // If it's still null, user probably hasn't set it.
            //noinspection UnusedAssignment
            jda = JDABuilder.create(token, INTENT_LIST)
                    // Add Listeners to JDA instance
                    .addEventListeners(
                            commandUtil,
                            onReadyListener,
                            guildMessageCommandListener,
                            guildMessageReactionListener,
                            banListListener
                    ).build();

            // TODO: Support multiple dbs? DBConnection should store variations of initializing them.
            // maybe some property like, switch (property) case mysql: initMysql, case postgreSql: initPostgre
            DBConnection.initMySqlConnection();
            dbconnection = DBConnection.getConnection();

            if (dbconnection == null) {
                logger.warn("Database connection couldn't be established.");
            }

        } catch (LoginException e) {
            logger.fatal("Could not initialize bot instance. Was token incorrect? {}", e.getMessage());
        }
    }
}
