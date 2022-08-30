package com.tut.nolebotv2core.util.social;

import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;

@AllArgsConstructor
public class TwitterConnector implements SocialMediaConnector {
    private final String apiKey;
    private final String apiSecret;
    private final String accessToken;
    private final String accessSecret;

    @Override
    public void post(Message m) {

    }
}
