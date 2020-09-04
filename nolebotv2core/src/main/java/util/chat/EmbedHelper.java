package util.chat;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.Event;

import java.awt.*;

public class EmbedHelper {
    public static EmbedBuilder getDefaultEmbedBuilder(Event event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("NoleBot", "https://github.com/Excaliburns/NoleBot", event.getJDA().getSelfUser().getAvatarUrl());
        embedBuilder.setFooter("NoleBot, a bot from Esports at Florida State", event.getJDA().getSelfUser().getAvatarUrl());
        embedBuilder.setColor(new Color(198, 77, 105));
        return embedBuilder;
    }

    public static MessageEmbed buildDefaultMessageEmbed(Event event, MessageEmbed.Field... fields) {
        EmbedBuilder embedBuilder = getDefaultEmbedBuilder(event);

        for (MessageEmbed.Field field :  fields) {
            embedBuilder.addField(field);
        }

        return embedBuilder.build();
    }
}
