package com.tut.nolebotshared.payloads;

import java.io.Serializable;

public record MemberAndGuildPayload(
        String guildId,
        String memberId
) implements Serializable { }
