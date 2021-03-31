package util.permissions;

import com.google.common.collect.ComparisonChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@AllArgsConstructor @Getter
public class GenericPermission implements Comparable<GenericPermission> {
    private final PermissionType type;

    private final String name;
    private final String snowflakeId;
    private final int permissionLevel;

    @Override
    public int compareTo(@NotNull GenericPermission o) {
        return ComparisonChain.start()
                .compare(permissionLevel, o.permissionLevel)
                .compare(name, o.getName())
                .result();
    }

}
