package com.tut.nolebotshared.payloads;

import java.io.Serializable;

public record GetRolesPayload(String guildId, String requesterUserId) implements Serializable {
}
