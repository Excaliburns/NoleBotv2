package util.permissions;

import com.google.common.collect.ComparisonChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class GenericPermission implements Comparable<GenericPermission> {
    //Type of permission, USER, GROUP, or ROLE
    private final PermissionType type;
    private final String name;
    //The UserID, GroupID, or RoleID for the holder of thr permission
    private final String snowflakeId;
    //The permissionLevel of the User, Group, or Role
    private final int permissionLevel;

    /**
     * Default Constructor.
     *
     * @param role Role to be used to construct the permission, namely it's Name and ID
     * @param permissionLevel Permission level for the permission object
     */
    public GenericPermission(final Role role, final int permissionLevel) {
        this.type = PermissionType.ROLE;
        this.name = role.getName();
        this.snowflakeId = role.getId();
        this.permissionLevel = permissionLevel;
    }

    @Override
    public int compareTo(@NotNull GenericPermission o) {
        return ComparisonChain.start()
                .compare(o.permissionLevel, permissionLevel)
                .compare(o.getName(), name)
                .result();
    }

}
