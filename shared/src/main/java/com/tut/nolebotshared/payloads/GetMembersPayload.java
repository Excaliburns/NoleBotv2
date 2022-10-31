package com.tut.nolebotshared.payloads;

import java.io.Serializable;

public record GetMembersPayload(String guildId, String requesterUserId, String search) implements Serializable {
}
