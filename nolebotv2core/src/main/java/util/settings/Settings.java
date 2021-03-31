package util.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.permissions.GenericPermission;

import java.time.Duration;
import java.util.*;

@Getter @Setter
public class Settings {
    private String prefix;
    private String guildId;
    private Duration attendanceTimer;
    private HashMap<String, Integer> commandPermissionMap;
    private TreeSet<GenericPermission> permissionList;
    private List<String> bannedUserIds;

    // Roles stored as IDs
    private List<String>             conditionalRole;
    // First String = roleID id to be assigned.
    // String List = roleIDs that can assign the role
    private Map<String, List<String>> roleOverrides;
    private boolean overrideRolePerms;
    private List<String> addableRoles;
    private boolean giveDevRole;
    private int devRolePosition;
    private int botRolePosition;
    // TODO: Name Verification

    private void initDefaults() {
        this.prefix          = "!";
        this.attendanceTimer = Duration.ofMinutes(5);
        overrideRolePerms    = false;
        bannedUserIds        = new ArrayList<>();
        roleOverrides        = new HashMap<String, List<String>>();
        addableRoles = new ArrayList<String>();
        giveDevRole = true;
        botRolePosition = 1;
        devRolePosition = botRolePosition - 1;
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
