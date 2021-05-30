package util.settings;

import lombok.Getter;
import lombok.Setter;
import util.permissions.GenericPermission;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

@Getter @Setter
public class Settings {
    //Fields stored in settings
    private String prefix;
    private String guildId;
    private Duration attendanceTimer;
    private HashMap<String, Integer> commandPermissionMap;
    private TreeSet<GenericPermission> permissionList;
    private List<String> bannedUserIds;

    // First String = roleID id to be assigned.
    // String List = roleIDs that can assign the role
    private HashMap<String, List<String>> roleOverrides;
    private HashSet<String> lockedRoles;
    // TODO: Name Verification
    //Initializes defaults that don't need to read anything from the server
    private void initDefaults() {
        this.prefix          = "!";
        this.attendanceTimer = Duration.ofMinutes(5);
        bannedUserIds        = new ArrayList<String>();
        roleOverrides        = new HashMap<>();
        lockedRoles         = new HashSet<>();
    }

    public Settings() {
        initDefaults();
    }

    public Settings(String guildID) {
        this.guildId = guildID;
        initDefaults();
    }
    //Adds a userID to the bannedUserIds list
    public void banUser(final String userToBanId) {
        this.bannedUserIds.add(userToBanId);
    }
    //Removes a userID from the bannedUserIds list
    public void unbanUser(final String userToUnbanId) {
        this.bannedUserIds.remove(userToUnbanId);
    }
    //Locks a role so it can't be assigned
    public void lockRole(String roleID) {
        lockedRoles.add(roleID);
    }
    public void unlockRole(String roleID) {
        lockedRoles.remove(roleID);
    }
    public void addPermission(GenericPermission p) {
        permissionList.add(p);
    }
}
