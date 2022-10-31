package com.tut.nolebotshared.entities;

import java.io.Serializable;

public record GuildAuthStatus(String guildId, boolean isAdmin, boolean isGameManager) implements Serializable {
}
