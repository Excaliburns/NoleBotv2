package commands.util;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

@Getter @Setter
public class CommandEvent {
    private List<String>   messageContent;
    private Guild          guild;
    private String         guildId;
    private MessageChannel channel;

    // TODO: Settings
    // TODO: Permissison Level
}
