package com.tut.nolebotshared.exceptions;

public class SnowflakeNotFoundException extends NoleBotException {
    public SnowflakeNotFoundException(final String errorMessage) { super (errorMessage); }
    public SnowflakeNotFoundException(final String errorMessage, final Throwable err) { super(errorMessage, err); }
}
