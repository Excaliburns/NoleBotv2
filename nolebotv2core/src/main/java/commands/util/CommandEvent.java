package commands.util;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import util.settings.Settings;

@Getter @Setter
public class CommandEvent {
    private Guild          guild;
    private String         guildId;
    private MessageChannel channel;
    private Settings       settings;


    public void sendErrorResponseToOriginatingChannel(String errorMessageContent) {
        channel.sendMessage("\u203C     " + errorMessageContent + "     \u203C").queue();
    }

    public void sendSuccessResponseToOriginatingChannel(String successMessageContent) {
        channel.sendMessage("\u2705 \u2014 " + successMessageContent).queue();
    }
}
