package com.tut.nolebotshared.payloads;

import com.tut.nolebotshared.entities.GuildAuthStatus;

import java.io.Serializable;
import java.util.List;

public record AuthStatusesPayload(GuildAuthStatus[] authStatuses, String userId) implements Serializable {
}
