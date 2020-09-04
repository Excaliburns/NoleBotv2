package util.permissions;

import lombok.Getter;
import lombok.Setter;

/**
 * Heierarchal Permission system
 * Explicitly set permissions in each level supersede the ones below
 *
 */
public enum PermissionType {
    USER,

    // TODO: IMPLEMENT CUSTOM GROUPS
    GROUP,

    ROLE,
}
