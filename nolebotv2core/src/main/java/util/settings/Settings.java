package util.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.permissions.GenericPermission;

import java.time.Duration;
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
    // Roles stored as IDs
    private List<String>             conditionalRole;
    // TODO: Name Verification

    public Settings() {
        this.prefix = "!";
        this.attendanceTimer = Duration.ofMinutes(5);
    }

    public Settings(String guildID) {
        this.guildId = guildID;
        this.prefix = "!";
        this.attendanceTimer = Duration.ofMinutes(5);
    }
}
