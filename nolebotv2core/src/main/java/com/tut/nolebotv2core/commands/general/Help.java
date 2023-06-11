package com.tut.nolebotv2core.commands.general;

import com.tut.nolebotv2core.commands.util.Command;
import com.tut.nolebotv2core.commands.util.CommandEvent;
import com.tut.nolebotv2core.commands.util.CommandUtil;
import com.tut.nolebotv2core.commands.util.ReactionCommand;
import com.tut.nolebotv2core.util.chat.EmbedHelper;
import com.tut.nolebotv2core.util.reactions.ReactionMessage;
import com.tut.nolebotv2core.util.reactions.ReactionMessageType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.ArrayList;
import java.util.List;

public class Help extends ReactionCommand {
    /**
     * Creates an instance of the help command.
     */
    public Help() {
        name                    = "help";
        description             = "Sends the help message. Also used to ask for help on other commands";
        helpDescription         = "I'm sorry, I can't help you with help!";
        requiredPermissionLevel = 0;
        usages.add("help");
    }

    @Override
    public void registerCommand(JDA jda) {
        jda.upsertCommand(Commands.slash(name, description)).queue();
    }
    @Override
    public void onCommandReceived(CommandEvent event) {
        event.getOriginatingJDAEvent().deferReply(true);
    }

    @Override
    public void executeCommand(CommandEvent event) {
        displayGenericHelpMenu(event);
    }

    private void displayGenericHelpMenu(CommandEvent event) {
        final ArrayList<MessageEmbed> commandHelpPages = new ArrayList<>();
        final ArrayList<Command> commands              = new ArrayList<>(CommandUtil.commands);

        final String prefix                            = event.getSettings().getPrefix();
        //Divide help command into pages of ten commands
        final int numPages                             = (commands.size() % 10 == 0) ? commands.size() / 10 :
                                                         (commands.size() / 10) + 1;

        for (int i = 1; i <= numPages; i++) {
            final int startIndex = (i - 1) * 10;
            final int endIndex   = commands.size() < 10 ? commands.size() : Math.min(i * 10, commands.size());

            // creat sublist of commands for each page
            final List<Command> commandPageSubList = commands.subList(startIndex, endIndex);
            final List<MessageEmbed.Field> fields = new ArrayList<>();

            // For each command in the sublist, append some info about it and add that to the pages list
            commandPageSubList.forEach(c -> {
                final String info1 = prefix + c.getName() + ": ";
                final String info2 = c.getDescription();

                fields.add(new MessageEmbed.Field(info1, info2, false));
            });

            fields.add(new MessageEmbed.Field("Page " + i + " of " + numPages, "", false));
            commandHelpPages.add(EmbedHelper.buildDefaultMessageEmbed(fields));
        }

        // Create ReactionMessage from sent messageEmbed
        final ReactionMessage reactionMessage = new ReactionMessage(
                ReactionMessageType.HELP_COMMAND,
                event.getChannel(),
                event.getOriginatingJDAEvent().getUser().getId(),
                event.getOriginatingJDAEvent().getId(),
                0,
                commandHelpPages,
                defaultEmojiCodeList
        );

        // Always send the first page when creating the display. Use the callback to populate the cache. Index is 0.
        this.sendFirstPage(
                event,
                commandHelpPages.get(0),
                commandHelpPages.size(),
                defaultEmojiCodeList,
                reactionMessage
        );
    }
}
