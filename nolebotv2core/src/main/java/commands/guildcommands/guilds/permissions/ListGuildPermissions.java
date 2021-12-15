package commands.guildcommands.guilds.permissions;

import commands.util.CommandEvent;
import commands.util.ReactionCommand;
import enums.EmojiCodes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import util.chat.EmbedHelper;
import util.permissions.GenericPermission;
import util.permissions.PermissionType;
import util.reactions.ReactionMessage;
import util.reactions.ReactionMessageCache;
import util.reactions.ReactionMessageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ListGuildPermissions extends ReactionCommand {
    public ListGuildPermissions() {
        this.description             = "Show the entities that have permission on your server";
        this.helpDescription         = "Shows the roles and users on your server that have a permission level, as well as their levels.";
        this.name                    = "listguildpermissions";
        this.requiredPermissionLevel = 1000;
    }

    @Override
    public void onCommandReceived(CommandEvent event) {
        final ArrayList<MessageEmbed> permissionPages = new ArrayList<>();
        final TreeSet<GenericPermission> permissionList  = event.getSettings().getPermissionList();
        //Maps permissionLevels to a Set that is in descending order
        //NavigableSet is just the return type of the method used to invert the TreeSet, could cast back to a TreeSet, or just a Set
        final NavigableSet<Integer> permissionLevels =
                //Maps permissions to a collection of ints
                permissionList.stream().map(permission -> permission.getPermissionLevel())
                //Stores collection in a TreeSet
                .collect(Collectors.toCollection(TreeSet::new))
                //Inverts the TreeSet so elements are in descending order
                .descendingSet();


        //For each permission level, make a new embed showing all users and roles who have that permission level
        permissionLevels.stream().forEach(i -> {
            EmbedBuilder builder = EmbedHelper.getDefaultEmbedBuilder();
            StringBuilder roleBuilder = new StringBuilder();
            StringBuilder userBuilder = new StringBuilder();
            builder.setTitle(String.valueOf(i));
            builder.setDescription(String.format("All entities with permission [%s]:", i));
            permissionList.stream().forEach(permission -> {
                if (permission.getPermissionLevel() == i) {
                    if (permission.getType() == PermissionType.ROLE) {
                        roleBuilder.append(permission.getName() + '\n');
                    }
                    if (permission.getType() == PermissionType.USER) {
                        userBuilder.append(permission.getName() + '\n');
                    }
                }
            });
            builder.addField("Roles: ", roleBuilder.toString(), false);
            builder.addField("Users: ", userBuilder.toString(), false);
            permissionPages.add(builder.build());

        });
        final List<EmojiCodes> helpReactions = Arrays.asList(
                EmojiCodes.PREVIOUS_ARROW,
                EmojiCodes.EXIT,
                EmojiCodes.NEXT_ARROW);

        // Create ReactionMessage from sent messageEmbed
        final ReactionMessage reactionMessage = new ReactionMessage(
                ReactionMessageType.PERMISSION_COMMAND,
                event.getChannel(),
                event.getOriginatingJDAEvent().getAuthor().getId(),
                event.getOriginatingJDAEvent().getMessageId(),
                0,
                permissionPages,
                helpReactions
        );

        // Always send the first page when creating the display. Use the callback to populate the cache. Index is 0.
        event.getChannel().sendMessage(permissionPages.get(0))
                .queue(message -> {
                    if (permissionPages.size() > 1) {
                        for (EmojiCodes helpReaction : helpReactions) {
                            if (helpReaction != EmojiCodes.PREVIOUS_ARROW) {
                                message.addReaction(helpReaction.unicodeValue).queue();
                            }
                        }
                        ReactionMessageCache.setReactionMessage(message.getId(), reactionMessage);
                    }
                });
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent event, ReactionMessage message, Message retrievedDiscordMessage) {
        int nextPage;

        // if left arrow
        if (event.getReactionEmote().getEmoji().equals(EmojiCodes.PREVIOUS_ARROW.unicodeValue)) {
            nextPage = message.getCurrentEmbedPage() - 1;
        }
        // if right arrow
        else if (event.getReactionEmote().getEmoji().equals(EmojiCodes.NEXT_ARROW.unicodeValue)) {
            nextPage = message.getCurrentEmbedPage() + 1;
        }
        else if (event.getReactionEmote().getEmoji().equals(EmojiCodes.EXIT.unicodeValue)) {
            retrievedDiscordMessage.editMessage(EmbedHelper.getDefaultExitMessage()).queue();
            retrievedDiscordMessage.clearReactions().queue();
            ReactionMessageCache.expireReactionMessage(retrievedDiscordMessage.getId());
            return;
        }
        // if other reaction
        else {
            return;
        }
        // If the next page is between 0 and the number of permission pages
        if (nextPage > -1 && nextPage < message.getEmbedList().size()) {
            //Set the permission message to the next page
            message.setCurrentEmbedPage(nextPage);
            //Clear previous reactions
            retrievedDiscordMessage.editMessage(message.getEmbedList().get(nextPage)).queue(editDone -> {
                editDone.clearReactions().queue();

                for (EmojiCodes emojiCode : message.getReactionsUsed()) {
                    // If the last embed page had a next arrow and we are on the last embed page
                    // Is checking the emojiCode necessary here?
                    boolean isLastPage = emojiCode == EmojiCodes.NEXT_ARROW && nextPage == message.getEmbedList().size() - 1;
                    // If the last embed page had a next arrow and we are on the first embed page
                    // Is checking the emojiCode necessary here?
                    boolean isFirstPage = emojiCode == EmojiCodes.PREVIOUS_ARROW && nextPage == 0;
                    //If we aren't on the first page or last page, add the reaction we are checking
                    //I think it would make more sense to check emojiCodes here.
                    if (!isLastPage && !isFirstPage) {
                        editDone.addReaction(emojiCode.unicodeValue).queue();
                    }
                }

                ReactionMessageCache.setReactionMessage(message.getMessageId(), message);
            });
        }
    }
}
