package util.settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class Settings {
    // TODO: RoleHelper
    private String prefix = "!";
    private String guildId;
    private List<?>                  roleHelperList; // TODO: RoleHelper
    private HashMap<String, Integer> commandPermissionMap;

    // Roles stored as IDs
    private List<String>             conditionalRole;
    // TODO: Attendance
    // TODO: Name Verification
    // TODO:


    public Settings(String guildID) {
        this.guildId = guildID;
    }
}
