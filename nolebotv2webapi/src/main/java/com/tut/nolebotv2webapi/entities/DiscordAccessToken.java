// CHECKSTYLE:OFF
package com.tut.nolebotv2webapi.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.micronaut.core.annotation.Introspected;
import lombok.Data;

import java.util.List;

@Data
@Introspected
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DiscordAccessToken {
    final String access_token;
    final String token_type;
    final Integer expires_in;
    final String refresh_token;
    final List<String> scope;
}
