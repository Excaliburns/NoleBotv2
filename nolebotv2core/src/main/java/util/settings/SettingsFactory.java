package util.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.FilesUtil;
import util.permissions.GenericPermission;
import util.permissions.PermissionType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SettingsFactory {
    private static final Logger logger = LogManager.getLogger(SettingsFactory.class);

    public static Settings getSettings(Guild guild) {
        return SettingsCache.settingsCache.getUnchecked(guild.getId());
    }

    /**
     * Get settings for guild by ID
     * @param guildId will be used as file path for guild
     * @return Settings object
     */
    public static Settings getSettingsForGuildFromFile(String guildId) {
        if (!SettingsManager.doesSettingsExistForGuild(guildId)) {
            logger.error("Settings was empty for guild {}, throwing exception. Create guild settings before trying to get them next time.", guildId);
            throw new NullPointerException(String.format("Guild settings do not exist for guild %s!", guildId));
        }
        else {
            logger.info("Found settings for guild with id {}", guildId);
            return FilesUtil.GSON_INSTANCE.fromJson(FilesUtil.getFileContentsAsString(getSettingsPathForGuild(guildId)), Settings.class);
        }
    }


    public static Path getSettingsPathForGuild(String guildID) {
        return Paths.get("config/" + guildID + "/settings.json");
    }
    /**
     * Initialize default settings object for guild
     * @param guild Admin roles from this guild will be initialized with permission level 1000, all roles will added to config
     * @return settings with default permissions, default addable roles and prefix
     */
    public static Settings initComplexDefaults(Guild guild) {
        Settings settings = new Settings(guild.getId());
        initDefaultPermissionListForGuild(guild, settings);
        initDefaultAssignableRoleListForGuild(guild, settings);
        initDefaultRoleOverrides(guild, settings);
        return settings;
    }
    /**
     * Initialize default permission list object for guild
     * @param guild Admin roles from this guild will be initialized with permission level 1000
     * @param s The settings object that the permission list will be saved to
     */
    private static void initDefaultPermissionListForGuild(Guild guild, Settings s) {
        List<Role> defaultAdminRoles = guild.getRoles()
                                            .stream()
                                            .filter(role -> role.hasPermission(Permission.ADMINISTRATOR))
                                            .collect(Collectors.toList());

        TreeSet<GenericPermission> defaultPermissions = defaultAdminRoles.stream()
                                                     .map(role -> new GenericPermission(PermissionType.ROLE, role.getName(), role.getId(), 1000))
                                                     .collect(Collectors.toCollection(TreeSet::new));
        s.setPermissionList(defaultPermissions);
        logger.info("Successfully created settings");
    }
    /**
     * Initialize default role list object for guild
     * @param guild all roles are default addable
     * @param s The settings object that the permission list will be saved to
     */
    private static void initDefaultAssignableRoleListForGuild(Guild guild, Settings s) {
        List<Role> serverRoles = guild.getRoles();
        List<String> listOfRoleIDs = new ArrayList<String>();
        serverRoles.stream().forEach((role) -> {
            listOfRoleIDs.add(role.getId());
        });
        s.setAddableRoles(listOfRoleIDs);
    }
    // This method is absolutely atrocious and is in need of significant reworks
    // It defaults the map of role overrides to allow every role to be assigned by a role with an equal or greater permission level
    // The main issue is that it is difficult to determine the integer representation of a roles permission level
    // Right now, I accomplish this by looping through every element of the permissionList Set in the Settings object, and comparing the snowflakeID to the roles currently being checked in the loops
    // Basically, this method gets a list of every role in the current guild, then checks every role against every role to determine if the permission level is greater or equal
    // I think to make this better we need a way to get a permLevel for a given RoleID.
    // That would at least take out the need for the forEach stream that loops through the PermissionList
    // Though to be fair, this method doesn't need to be extremely performant, considering how rarely it will be run
    private static void initDefaultRoleOverrides(Guild guild, Settings s) {
        HashMap<String, List<String>> settingsMap = new HashMap<String, List<String>>();
        List<String> allRoles = new ArrayList<String>();
        guild.getRoles().stream().forEach(role -> {
            allRoles.add(role.getId());
        });
        for (int i = 0; i < allRoles.size(); i++) {
            List<String> roleIDsThatCanExcecute = new ArrayList<String>();
            for (int k = 0; k < allRoles.size(); k++) {
                String roleIDOfI = allRoles.get(i);
                String roleIDOfK = allRoles.get(k);
                int finalK = k;
                s.getPermissionList().stream().forEach(genericPermission -> {
                    Integer permLevelOfRoleI = null;
                    Integer permLevelOfRoleK = null;
                    if (genericPermission.getSnowflakeId().equals(roleIDOfI)){
                        permLevelOfRoleI = genericPermission.getPermissionLevel();
                    }
                    if (genericPermission.getSnowflakeId().equals(roleIDOfK)) {
                        permLevelOfRoleK = genericPermission.getPermissionLevel();
                    }
                    if (permLevelOfRoleI != null && permLevelOfRoleK != null && permLevelOfRoleK >= permLevelOfRoleI) {
                        roleIDsThatCanExcecute.add(allRoles.get(finalK));
                    }
                });

            }
            settingsMap.put(allRoles.get(i), roleIDsThatCanExcecute);
        }
        s.setRoleOverrides(settingsMap);
    }
}
