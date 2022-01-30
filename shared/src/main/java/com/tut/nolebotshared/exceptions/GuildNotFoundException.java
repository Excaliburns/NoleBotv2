package com.tut.nolebotshared.exceptions;

public class GuildNotFoundException extends SnowflakeNotFoundException {
    public GuildNotFoundException(final String errorMessage) {
        super(errorMessage);
    }

    public GuildNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
