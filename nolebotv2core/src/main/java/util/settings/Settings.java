package util.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
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

    // Roles stored as IDs
    private List<String>             conditionalRole;
    // TODO: Name Verification

    private void initDefaults() {
        this.prefix          = "!";
        this.attendanceTimer = Duration.ofMinutes(5);
        bannedUserIds        = new ArrayList<>();
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
