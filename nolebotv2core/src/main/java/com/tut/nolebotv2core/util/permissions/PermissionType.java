package com.tut.nolebotv2core.util.permissions;

/**
 * Hierarchical Permission system.
 * Explicitly set permissions in each level supersede the ones below
 *
 */
public enum PermissionType {
    USER,

    // TODO: IMPLEMENT CUSTOM GROUPS
    GROUP,

    ROLE,
}
