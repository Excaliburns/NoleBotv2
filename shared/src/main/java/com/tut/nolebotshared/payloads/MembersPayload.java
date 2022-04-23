package com.tut.nolebotshared.payloads;

import com.tut.nolebotshared.entities.GuildUser;
import java.io.Serializable;
import java.util.ArrayList;

public record MembersPayload(ArrayList<GuildUser> users, int numPages) implements Serializable {
}
