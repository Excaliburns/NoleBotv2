package commands.general;

import com.google.common.primitives.Ints;
import commands.util.Command;
import commands.util.CommandEvent;
import commands.util.CommandUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import util.chat.EmbedHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Help extends Command {

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
    public void onCommandReceived(CommandEvent event) throws Exception {
        final boolean isGenericHelpCommand = event.getMessageContent().size() == 1 ||
                                             Objects.nonNull(Ints.tryParse(event.getMessageContent().get(1)));

        if (isGenericHelpCommand) {
            try {
                event.getChannel().sendMessage(displayGenericHelpMenu(event)).queue();
            }
            catch (IndexOutOfBoundsException e) {
                //Not sure if its necessary to make this error message into a MessageEmbed
                event.sendErrorResponseToOriginatingChannel("That help page doesn't exist!");
            }
        }
        else if (event.getMessageContent().size() == 2) {
            event.getChannel().sendMessage(sendSpecificCommandHelp(event)).queue();
        }
    }

    private MessageEmbed sendSpecificCommandHelp(CommandEvent event) {
        // Uses the map of commands in CommandUtil to match the second word of the message to a command
        // event.getCommand() doesn't work here
        final String calledCommandName          = event.getMessageContent().get(1);
        final Optional<Command> commandOptional = CommandUtil.getCommandFromMap(calledCommandName);

        if (commandOptional.isPresent()) {
            final Command calledCommand = commandOptional.get();
            final String prefix         = event.getSettings().getPrefix();
            final String commandString  = prefix + calledCommand.getName();

            final StringBuilder usages = new StringBuilder();
            calledCommand.getUsages().forEach( usage -> usages.append(prefix).append(usage).append("\n"));

            List<MessageEmbed.Field> fieldList = new ArrayList<>();
            fieldList.add(new MessageEmbed.Field("Command Name: ",               commandString,                                                true));
            fieldList.add(new MessageEmbed.Field("Permission Level Required: ",    String.valueOf(calledCommand.getRequiredPermissionLevel()), true));
            fieldList.add(new MessageEmbed.Field("Description: ",                 calledCommand.getHelpDescription(),                          false));
            fieldList.add(new MessageEmbed.Field("Usages <required> [optional]:", usages.toString(),                                           false));

            return EmbedHelper.buildDefaultMessageEmbed(event.getOriginatingJDAEvent(), fieldList);
        } else {
            final MessageEmbed.Field field = new MessageEmbed.Field("Error!", "No command with that name found!", false);
            return EmbedHelper.buildDefaultMessageEmbed(event.getOriginatingJDAEvent(), field);
        }
    }

    private MessageEmbed displayGenericHelpMenu(CommandEvent event) {
        final ArrayList<Command> commands = CommandUtil.commands;
        final String prefix               = event.getSettings().getPrefix();
        final int pageNum                 = event.getMessageContent().size() == 1 ? 1 : Integer.parseInt(event.getMessageContent().get(1));
        final int numPages                = (commands.size() % 10 == 0) ? commands.size() / 10 : (commands.size() / 10) + 1;

        if (pageNum > numPages) {
            throw new IndexOutOfBoundsException();
        }

        //Starting index of the loop
        final int startIndex                  = (pageNum - 1) * 10;
        //End index of the loop. If there are less than 10 commands, loop through them all. Otherwise, only loop through 10 commands
        final int endIndex                    = commands.size() < 10 ? commands.size() : startIndex + 10;

        // Starts at the startIndex command, then creates a field with the command name and the description.
        // Adds it to the list of fields, then loops for 10 commands (or less if there aren't 10 commands registered).
        final List<MessageEmbed.Field> fields = new ArrayList<>();

        for (int i = startIndex; i < endIndex; i++) {
            final String info1 = prefix + commands.get( i+((pageNum-1)*10) ).getName() + ": ";
            final String info2 = commands.get(i).getDescription();

            fields.add(new MessageEmbed.Field(info1, info2, false));
        }
        return EmbedHelper.buildDefaultMessageEmbed(event.getOriginatingJDAEvent(), fields);
    }
}
