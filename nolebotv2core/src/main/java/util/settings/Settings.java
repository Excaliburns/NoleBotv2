package util.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.permissions.GenericPermission;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

@Getter @Setter
@NoArgsConstructor
public class Settings {
    private String prefix = "!";
    private String guildId;
    private HashMap<String, Integer> commandPermissionMap;
    private TreeSet<GenericPermission> permissionList;

    // Roles stored as IDs
    private List<String>             conditionalRole;
    // TODO: Attendance
    // TODO: Name Verification
    // TODO:


    public Settings(String guildID) {
        this.guildId = guildID;
    }
}
