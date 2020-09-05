package commands.general;

import commands.util.Command;
import commands.util.CommandEvent;
import commands.util.CommandUtil;
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
        usages.add("help [1-100]");
    }

    @Override
    public void onCommandReceived(CommandEvent event) throws Exception {
    	//You can replace the isNumber method with String.matches(regex) but I couldn't get the right regex
    	//This logic will make it impossible to have a command only made of numbers, but I don't think that will be a problem
        if (event.getMessageContent().size() == 1 || isNumber(event.getMessageContent().get(1))) {
        	try {
        		event.getChannel().sendMessage(sendHelpCommand(event)).queue();
        	}
        	catch (IndexOutOfBoundsException e)
        	{
        		//Not sure if its necessary to make this error message into a MessageEmbed
        		event.getChannel().sendMessage("That help page doesn't exist!").queue();
        	}
        }
        else if (event.getMessageContent().size() == 2) {
            event.getChannel().sendMessage(sendHelpSubcommand(event)).queue();
        }
    }

    private MessageEmbed sendHelpSubcommand(CommandEvent event) {
    	// Uses the map of commands in CommandUtil to match the second word of the message to a command
        final Command calledCommand = CommandUtil.commands.get(CommandUtil.commandIndex.get(event.getMessageContent().get(1)));
        		// event.getCommand() doesn't work here
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
    private MessageEmbed sendHelpCommand(CommandEvent event) {
    	final String prefix             = event.getSettings().getPrefix();
    	final int pageNum               = event.getMessageContent().size() == 1 ? 1 : Integer.valueOf(event.getMessageContent().get(1));
    	ArrayList<Command> commands     = CommandUtil.commands;
    	final int numPages              = commands.size() % 10 == 0 ? commands.size() / 10 : (commands.size() / 10) + 1;
    	String info1                    = "";
    	String info2                    = "";
    	//Starting index of the loop
    	int startIndex                  = (pageNum - 1) * 10;
    	//End index of the loop. If there are less than 10 commands, loop through them all. Otherwise, only loop through 10 commands
    	int endIndex                    = commands.size() < 10 ? commands.size() : startIndex + 10;
    	List<MessageEmbed.Field> fields = new ArrayList<>();
    	if (pageNum > numPages) {
    		throw new IndexOutOfBoundsException();
    	}
    	// Starts at the startIndex command, then creates a field with the command name and the description.
    	// Adds it to the list of fields, then loops for 10 commands (or less if there aren't 10 commands registered).
    	for (int i = startIndex; i < endIndex; i++) {
    		info1 = prefix + commands.get(i+((pageNum-1)*10)).getName() + ": ";
    		info2 = commands.get(i).getDescription();
    		MessageEmbed.Field temp = new MessageEmbed.Field(info1, info2, false);
    		fields.add(temp);
    	}
    	return EmbedHelper.buildDefaultMessageEmbed(event.getOriginatingJDAEvent(), fields);
    }
    private boolean isNumber(String s) {
    	String numbers = "0123456789";
    	for (int i = 0; i < s.length(); i++) {
    		if (!numbers.contains(""+s.charAt(i))){
    			return false;
    		}
    	}
    	return true;
    }
}
