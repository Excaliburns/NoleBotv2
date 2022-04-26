// CHECKSTYLE:OFF
package com.tut.nolebotshared.entities;

import java.io.Serializable;


public record DiscordUser(
        String id,
        String username,
        String discriminator,
        String avatar,
        Boolean bot,
        Boolean system,
        Boolean mfa_enabled,
        String banner,
        String accent_color,
        String locale,
        Boolean verified,
        Boolean email,
        Integer flags,
        Integer premium_type,
        Integer public_flags
) implements Serializable{
}
