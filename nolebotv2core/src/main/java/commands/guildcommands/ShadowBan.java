package commands.guildcommands;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import util.chat.EmbedHelper;
import util.settings.Settings;
import util.settings.SettingsCache;

import java.util.List;

public class ShadowBan extends Command {

    /**
     * Default Constructor.
     */
    public ShadowBan() {
        name                    = "shadowban";
        description             = "Shadow-bans or unbans a user";
        helpDescription         = "Shadow-bans a user, preventing them from joining voice channels or chatting";
        requiredPermissionLevel = 1000;
        usages.add("shadowban [@users]");
        usages.add("shadowban list");
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        final Settings settings = event.getSettings();

        if (event.getMessageContent().contains("list")) {
            if (settings.getBannedUserIds().isEmpty()) {
                event.sendErrorResponseToOriginatingChannel("You have no banned users.");
            }
            else {
                final EmbedBuilder builder = EmbedHelper.getDefaultEmbedBuilder();
                final List<String> bannedUsers = settings.getBannedUserIds();
                for (int i = 0; i < bannedUsers.size(); i++) {
                    if (i == 0) {
                        builder.addField("Banned Users", "<@" + bannedUsers.get(i) + ">", true);
                    }
                    else {
                        builder.addField("", "<@" + bannedUsers.get(i) + ">", true);
                    }
                }

                event.getChannel().sendMessageEmbeds(builder.build()).queue();
            }

        }
        else {
            final List<Member> membersMentioned = event.getOriginatingJDAEvent().getMessage().getMentionedMembers();
            final MessageChannel channel = event.getChannel();

            membersMentioned.forEach(member -> {
                if (settings.getBannedUserIds().contains(member.getId())) {
                    settings.unbanUser(member.getId());

                    channel.sendMessage("Unbanned " + member.getAsMention()).queue();
                }
                else {
                    settings.banUser(member.getId());
                    channel.sendMessage("Shadow-banned " + member.getAsMention()).queue();
                }
            });

            SettingsCache.saveSettingsForGuild(event.getGuild(), settings);
        }
    }
}
