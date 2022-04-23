package com.tut.nolebotshared.payloads;

import java.io.Serializable;

public record GetMembersPayload(String guildId, int pageNum) implements Serializable {
}
