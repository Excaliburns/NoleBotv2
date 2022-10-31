package com.tut.nolebotshared.entities;

import java.io.Serializable;

public record GuildRole(
        String id,
        String name,
        Integer color,
        String iconLink,
        Boolean isAtEveryone
) implements Serializable { }
