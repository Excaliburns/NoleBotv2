//CHECKSTYLE:OFF
package com.tut.nolebotv2webapi.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscordAccessCodeRequest {
    private String client_id;
    private String client_secret;
    private String grant_type;
    private String code;
    private String redirect_uri;
}
