// CHECKSTYLE:OFF
package com.tut.nolebotv2webapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DiscordAccessTokenRequestDto {
    private final String client_id;
    private final String client_secret;
    private final String grant_type;
    private final String code;
    private final String redirect_uri;
}
