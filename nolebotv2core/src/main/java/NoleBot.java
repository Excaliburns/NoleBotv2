import enums.PropEnum;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.PropertiesUtil;

import javax.security.auth.login.LoginException;
import java.util.List;

public class NoleBot {
    private static Logger logger = LogManager.getLogger(NoleBot.class);

    public static void main(String[] args) {
        // Enable specific events from discord gateway.
        final List<GatewayIntent> INTENT_LIST = List.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
        );

        JDA jda;

        try {
            jda = JDABuilder.create(PropertiesUtil.getProperty(PropEnum.TOKEN), INTENT_LIST).build();

            logger.info("JDA Started! End.");
            jda.shutdown();
        } catch (LoginException e) {
            logger.error("Could not initalize bot instance. Was token incorrect? {}", e.getMessage());
        }


        // Create bot instance.
    }
}
