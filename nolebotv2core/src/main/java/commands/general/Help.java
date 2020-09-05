package commands.general;

import java.util.Collections;

import commands.util.Command;
import commands.util.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Help extends Command {

	public Help() 
	{
		name                    = "help";
        description             = "Sends the help message. Also used to ask for help on other commands.";
        helpDescription         = "I'm sorry, I can't help you with help!";
        requiredPermissionLevel = 0;
        usages.add("help");
        usages.add("help [command]");
	}

	@Override
	public void onCommandReceived(CommandEvent event) throws Exception 
	{
		
		if (event.getMessageContent().size() == 2)
		{
			Command calledCommand = commands.util.CommandUtil.commands.get(commands.util.CommandUtil.commandIndex.get(event.getMessageContent().get(1)));
			String commandString = calledCommand.getName();
			event.getChannel().sendMessage(sendHelpSubcommand(event)).queue();
		}
		
        
	}
	public MessageEmbed sendHelpSubcommand(CommandEvent event)
	{
		Command calledCommand = commands.util.CommandUtil.commands.get(commands.util.CommandUtil.commandIndex.get(event.getMessageContent().get(1)));
		String prefix = event.getSettings().getPrefix();
		String commandString =  prefix + calledCommand.getName();
		EmbedBuilder builder = new EmbedBuilder();
		String usages = "";
		builder.addField("Command Name: ", commandString, true);
        builder.addField("Permission Level Required: ", String.valueOf(calledCommand.getRequiredPermissionLevel()), true);
        builder.addField("Description: ", calledCommand.getHelpDescription(), false);
        builder.setAuthor("NoleBotv2", "https://github.com/Excaliburns/NoleBot");
        //Temporary Color until I find the right one
        builder.setColor(1);
        builder.setFooter("NoleBot, a bot from Esports at Florida State", enums.PropEnum.AVATAR);
        
        for (String s : calledCommand.getUsages())
        {
        	usages += prefix + s + "\n";
        }
        builder.addField("Usages <required> [optional]:", usages, false);
        
        return builder.build();
	}
}
