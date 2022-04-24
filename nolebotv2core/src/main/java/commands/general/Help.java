package commands.general;

import com.google.common.primitives.Ints;
import commands.util.Command;
import commands.util.CommandEvent;
import commands.util.CommandUtil;
import commands.util.ReactionCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import util.chat.EmbedHelper;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Help extends ReactionCommand {
    /**
     * Creates an instance of the help command.
     */
    public Help() {
        name                    = "help";
        description             = "Sends the help message. Also used to ask for help on other commands.";
        helpDescription         = "I'm sorry, I can't help you with help!";
        requiredPermissionLevel = 0;
        usages.add("help");
        usages.add("help [command]");
        usages.add("help [1-100]");
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onCommandReceived(CommandEvent event) {
        // Whether to execute the generic help command or to get a specific help page
        final boolean isGenericHelpCommand = event.getMessageContent().size() == 1 ||
                                             //If the 2nd word can be parsed to an int
                                             Objects.nonNull(Ints.tryParse(event.getMessageContent().get(1)));

        if (isGenericHelpCommand) {
            try {
                displayGenericHelpMenu(event);
            }
            catch (IndexOutOfBoundsException e) {
                //Not sure if It's necessary to make this error message into a MessageEmbed
                event.sendErrorResponseToOriginatingChannel("That help page doesn't exist!");
            }
        }
        else if (event.getMessageContent().size() == 2) {
            event.getChannel().sendMessageEmbeds(sendSpecificCommandHelp(event)).queue();
        }
    }

    private MessageEmbed sendSpecificCommandHelp(CommandEvent event) {
        // Uses the map of commands in CommandUtil to match the second word of the message to a command
        // event.getCommand() doesn't work here
        final String calledCommandName          = event.getMessageContent().get(1);
        final Optional<Command> commandOptional = CommandUtil.getCommandFromMap(calledCommandName);
        //If the command exists
        if (commandOptional.isPresent()) {
            final Command calledCommand = commandOptional.get();
            final String prefix         = event.getSettings().getPrefix();
            //The exact string that would be entered to call the command
            final String commandString  = prefix + calledCommand.getName();

            //Make a string of usages, with each usage on its own line
            final StringBuilder usages = new StringBuilder();
            calledCommand.getUsages().forEach(usage -> usages.append(prefix).append(usage).append("\n"));

            List<MessageEmbed.Field> fieldList = new ArrayList<>();
            fieldList.add(
                    new MessageEmbed.Field(
                            "Command Name: ",
                            commandString,
                            true
                    )
            );
            fieldList.add(
                    new MessageEmbed.Field(
                            "Permission Level Required: ",
                            String.valueOf(calledCommand.getRequiredPermissionLevel()),
                            true
                    )
            );
            fieldList.add(
                    new MessageEmbed.Field(
                            "Description: ",
                            calledCommand.getHelpDescription(),
                            false
                    )
            );
            fieldList.add(
                    new MessageEmbed.Field(
                            "Usages <required> [optional]: ",
                            usages.toString(),
                            false
                    )
            );

            return EmbedHelper.buildDefaultMessageEmbed(fieldList);
        }
        else {
            final MessageEmbed.Field field = new MessageEmbed.Field(
                    "Error!",
                    "No command with that name found!",
                    false
            );
            return EmbedHelper.buildDefaultMessageEmbed(field);
        }
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
                event.getOriginatingJDAEvent().getAuthor().getId(),
                event.getOriginatingJDAEvent().getMessageId(),
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
