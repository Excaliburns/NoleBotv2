package util.chat;

import enums.PropEnum;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import util.PropertiesUtil;

import java.awt.*;
import java.util.List;

public class EmbedHelper {
    /**
     * Gets the default EmbedBuilder template for adding new fields to.
     * @return a populated template EmbedBuilder. Must be converted to MessageEmbed.
     */
    public static EmbedBuilder getDefaultEmbedBuilder() {
        final String BOT_AVATAR_URL     = PropertiesUtil.getProperty(PropEnum.BOT_AVATAR_URL);
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor("NoleBot", "https://github.com/Excaliburns/NoleBotv2", BOT_AVATAR_URL);
        embedBuilder.setFooter("NoleBot, a bot from Esports at Florida State", BOT_AVATAR_URL);
        embedBuilder.setColor(new Color(198, 77, 105));
        return embedBuilder;
    }

    /**
     * Builds the default message embed from a variable amount of fields.
     * @param fieldList Fields that will populate the new MessageEmbed
     * @return MessageEmbed with fields defined in params
     */
    public static MessageEmbed buildDefaultMessageEmbed(List<MessageEmbed.Field> fieldList) {
        return buildDefaultMessageEmbed(fieldList.toArray(new MessageEmbed.Field[0]));
    }

    /**
     * Builds the default message embed from a variable amount of fields.
     * @param fields Fields that will populate the new MessageEmbed
     * @return MessageEmbed with fields defined in params
     */
    public static MessageEmbed buildDefaultMessageEmbed(MessageEmbed.Field... fields) {
        EmbedBuilder embedBuilder = getDefaultEmbedBuilder();

        for (MessageEmbed.Field field :  fields) {
            embedBuilder.addField(field);
        }

        return embedBuilder.build();
    }

    /**
     * Get default expiry message for reactionMessage events.
     * @return default MessageEmbed page that states their query has expired.
     */
    public static MessageEmbed getDefaultExpiryReactionMessage() {
        return EmbedHelper.buildDefaultMessageEmbed(
                new MessageEmbed.Field("Uh oh!", "Your query has timed out. Please use your command again.", false)
        );
    }
}
