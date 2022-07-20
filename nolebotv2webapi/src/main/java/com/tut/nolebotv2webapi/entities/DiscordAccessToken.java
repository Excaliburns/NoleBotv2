// CHECKSTYLE:OFF
package com.tut.nolebotv2webapi.entities;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Introspected
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DiscordAccessToken {
    String access_token;
    String token_type;
    String expires_in;
    String refresh_token;
    String scopes;
}
