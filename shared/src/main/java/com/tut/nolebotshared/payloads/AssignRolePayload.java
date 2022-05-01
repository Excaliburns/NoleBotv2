package com.tut.nolebotshared.payloads;

import java.io.Serializable;
import java.util.List;

public record AssignRolePayload(List<String> roleIds, List<String> userIds, String guildId) implements Serializable {
}
