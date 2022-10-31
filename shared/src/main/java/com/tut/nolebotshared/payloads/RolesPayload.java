package com.tut.nolebotshared.payloads;

import com.tut.nolebotshared.entities.GuildRole;

import java.io.Serializable;
import java.util.List;

public record RolesPayload(List<GuildRole> roles) implements Serializable {
}
