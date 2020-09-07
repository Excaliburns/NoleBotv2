import commands.general.Help;
import commands.guildcommands.HelloWorld;
import commands.guildcommands.guilds.SetPrefix;
import commands.guildcommands.guilds.permissions.ListGuildPermissions;
import listeners.GuildMessageCommandListener;
import commands.util.CommandUtil;
import enums.PropEnum;
import listeners.ReactionListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.PropertiesUtil;

import javax.security.auth.login.LoginException;
import java.util.List;

public class NoleBot {
    private static Logger logger = LogManager.getLogger(NoleBot.class);
    private static final CommandUtil                 commandUtil                 = new CommandUtil();
    private static final GuildMessageCommandListener guildMessageCommandListener = new GuildMessageCommandListener();
    private static final ReactionListener            reactionListener            = new ReactionListener();


    public static void main(String[] args) {
        // Enable specific events from discord gateway.
        final List<GatewayIntent> INTENT_LIST = List.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
        );

        JDA jda;

        // Add Commands
        commandUtil.addCommand(new Help());
        commandUtil.addCommand(new HelloWorld());
        commandUtil.addCommand(new SetPrefix());
        commandUtil.addCommand(new ListGuildPermissions());

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
            jda = JDABuilder.create(token, INTENT_LIST)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                    .build();

            // Add Listeners to JDA instance
            jda.addEventListener(
                    commandUtil,
                    guildMessageCommandListener,
                    reactionListener
            );
        } catch (LoginException e) {
            logger.error("Could not initalize bot instance. Was token incorrect? {}", e.getMessage());
        }


        // Create bot instance.
    }
}
