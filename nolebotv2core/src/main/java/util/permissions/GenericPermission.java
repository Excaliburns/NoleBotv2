package util.permissions;

import com.google.common.collect.ComparisonChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@AllArgsConstructor @Getter
public class GenericPermission implements Comparable<GenericPermission> {
    //Type of permission, USER, GROUP, or ROLE
    private final PermissionType type;
    private final String name;
    //The UserID, GroupID, or RoleID for the holder of thr permission
    private final String snowflakeId;
    //The permissionLevel of the User, Group, or Role
    private final int permissionLevel;

    @Override
    public int compareTo(@NotNull GenericPermission o) {
        return ComparisonChain.start()
                .compare(permissionLevel, o.permissionLevel)
                .compare(name, o.getName())
                .result();
    }

}
