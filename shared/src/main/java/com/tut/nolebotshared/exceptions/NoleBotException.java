package com.tut.nolebotshared.exceptions;

public class NoleBotException extends Exception {

    public NoleBotException() {
    }

    public NoleBotException(final String errorMsg) {
        super(errorMsg);
    }

    public NoleBotException(final String errorMsg, final Throwable err) {
        super(errorMsg, err);
    }
}
