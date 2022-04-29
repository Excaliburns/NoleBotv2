package com.tut.nolebotshared.payloads;

import com.tut.nolebotshared.entities.GuildUser;

import java.io.Serializable;

public record IdPayload(String userId) implements Serializable {
}
