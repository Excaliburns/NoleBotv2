package com.tut.nolebotshared.entities;

import java.io.Serializable;
import java.util.List;

public record GuildUser(
        String id,
        String nickname,
        String discordTag,
        List<GuildRole> roles,
        String avatarUrl
) implements Serializable { }
