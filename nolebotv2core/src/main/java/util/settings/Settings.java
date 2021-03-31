package util.settings;

import lombok.Getter;
import lombok.Setter;
import util.permissions.GenericPermission;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

@Getter @Setter
public class Settings {
    private String prefix;
    private String guildId;
    private Duration attendanceTimer;
    private HashMap<String, Integer> commandPermissionMap;
    private TreeSet<GenericPermission> permissionList;
    private List<String> bannedUserIds;
    // First String = roleID id to be assigned.
    // String List = roleIDs that can assign the role
    private HashMap<String, List<String>> roleOverrides;
    private List<String> addableRoles;
    // TODO: Name Verification

    private void initDefaults() {
        this.prefix          = "!";
        this.attendanceTimer = Duration.ofMinutes(5);
        bannedUserIds        = new ArrayList<>();
        roleOverrides        = new HashMap<>();
        addableRoles         = new ArrayList<>();
    }

    public Settings() {
        initDefaults();
    }

    public Settings(String guildID) {
        this.guildId = guildID;
        initDefaults();
    }

    public void banUser(final String userToBanId) {
        this.bannedUserIds.add(userToBanId);
    }

    public void unbanUser(final String userToUnbanId) {
        this.bannedUserIds.remove(userToUnbanId);
    }
}
