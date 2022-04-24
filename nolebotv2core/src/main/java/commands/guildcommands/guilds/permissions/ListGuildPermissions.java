package commands.guildcommands.guilds.permissions;

import commands.util.CommandEvent;
import commands.util.ReactionCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import util.chat.EmbedHelper;
import util.permissions.GenericPermission;
import util.permissions.PermissionType;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageType;

import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ListGuildPermissions extends ReactionCommand {
    /**
     * Default Constructor.
     */
    public ListGuildPermissions() {
        this.description             = "Show the entities that have permission on your server";
        this.helpDescription         = "Shows the roles and users on your server that have a permission level, " +
                                       "as well as their levels.";
        this.name                    = "listguildpermissions";
        this.requiredPermissionLevel = 1000;
    }

    @Override
    public void onCommandReceived(CommandEvent event) {
        final ArrayList<MessageEmbed> permissionPages = new ArrayList<>();
        final TreeSet<GenericPermission> permissionList = new TreeSet<>(event.getSettings().getPermissionList());
        //Maps permissionLevels to a Set that is in descending order
        //NavigableSet is just the return type of the method used to invert the TreeSet,
        //could cast back to a TreeSet, or just a Set
        final NavigableSet<Integer> permissionLevels =
                //Maps permissions to a collection of ints
                permissionList.stream().map(GenericPermission::getPermissionLevel)
                //Stores collection in a TreeSet
                .collect(Collectors.toCollection(TreeSet::new))
                //Inverts the TreeSet so elements are in descending order
                .descendingSet();


        //For each permission level, make a new embed showing all users and roles who have that permission level
        permissionLevels.forEach(i -> {
            EmbedBuilder builder = EmbedHelper.getDefaultEmbedBuilder();
            StringBuilder roleBuilder = new StringBuilder();
            StringBuilder userBuilder = new StringBuilder();
            builder.setTitle(String.valueOf(i));
            builder.setDescription(String.format("All entities with permission [%s]:", i));
            permissionList.forEach(permission -> {
                if (permission.getPermissionLevel() == i) {
                    if (permission.getType() == PermissionType.ROLE) {
                        roleBuilder.append(permission.getName()).append('\n');
                    }
                    if (permission.getType() == PermissionType.USER) {
                        userBuilder.append(permission.getName()).append('\n');
                    }
                }
            });
            builder.addField("Roles: ", roleBuilder.toString(), false);
            builder.addField("Users: ", userBuilder.toString(), false);
            permissionPages.add(builder.build());

        });

        // Create ReactionMessage from sent messageEmbed
        final ReactionMessage reactionMessage = new ReactionMessage(
                ReactionMessageType.PERMISSION_COMMAND,
                event.getChannel(),
                event.getOriginatingJDAEvent().getAuthor().getId(),
                event.getOriginatingJDAEvent().getMessageId(),
                0,
                permissionPages,
                defaultEmojiCodeList
        );

        this.sendFirstPage(
                event,
                permissionPages.get(0),
                permissionPages.size(),
                defaultEmojiCodeList,
                reactionMessage
        );
    }
}
