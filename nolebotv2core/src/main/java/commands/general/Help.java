package commands.general;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import util.chat.EmbedHelper;

import java.util.ArrayList;
import java.util.List;

public class Help extends Command {

    public Help() {
        name                    = "help";
        description             = "Sends the help message. Also used to ask for help on other commands.";
        helpDescription         = "I'm sorry, I can't help you with help!";
        requiredPermissionLevel = 0;
        usages.add("help");
        usages.add("help [command]");
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
        if (event.getMessageContent().size() == 2) {
            event.getChannel().sendMessage(sendHelpSubcommand(event)).queue();
        }
    }

    private MessageEmbed sendHelpSubcommand(CommandEvent event) {
        final Command calledCommand = event.getCommand();
        final String prefix         = event.getSettings().getPrefix();
        final String commandString  = prefix + calledCommand.getName();

        final StringBuilder usages = new StringBuilder();
        calledCommand.getUsages().forEach( usage -> usages.append(prefix).append(usage).append("\n"));

        List<MessageEmbed.Field> fieldList = new ArrayList<>();
        fieldList.add(new MessageEmbed.Field("Command Name: ", 			    commandString, 										       true));
        fieldList.add(new MessageEmbed.Field("Permission Level Required: ",   String.valueOf(calledCommand.getRequiredPermissionLevel()),true));
        fieldList.add(new MessageEmbed.Field("Description: ", 			    calledCommand.getHelpDescription(), 				       false));
        fieldList.add(new MessageEmbed.Field("Usages <required> [optional]:", usages.toString(),                                         false));

        return EmbedHelper.buildDefaultMessageEmbed(event.getOriginatingJDAEvent(), fieldList);
    }
}
