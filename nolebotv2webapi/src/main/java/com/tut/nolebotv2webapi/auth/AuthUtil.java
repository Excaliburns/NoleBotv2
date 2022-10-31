package com.tut.nolebotv2webapi.auth;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import com.tut.nolebotshared.entities.GuildAuthStatus;
import io.micronaut.security.authentication.Authentication;

import java.util.ArrayList;

public class AuthUtil {
    //This method is a little disgusting, but it seems Micronaut can't deserialize a JWT claim of a list of POJOs,
    // so we have to do it our self.
    public static GuildAuthStatus getAuthStatus(Authentication authentication, String guildId) {
        ArrayList<LinkedTreeMap<String, Object>> authStatusMaps = (ArrayList<LinkedTreeMap<String, Object>>) authentication.getAttributes().get("auth_statuses");
        ArrayList<GuildAuthStatus> authStatuses = new ArrayList<>();
        authStatusMaps.forEach(map -> {
            authStatuses.add(new GuildAuthStatus((String) map.get("guildId"), (Boolean) map.get("isAdmin"), (Boolean) map.get("isGameManager")));
        });
        GuildAuthStatus authStatus = authStatuses.stream().filter((status) -> {
            return status.guildId().equals(guildId);
        }).toList().get(0);
        return authStatus;
    }
}
