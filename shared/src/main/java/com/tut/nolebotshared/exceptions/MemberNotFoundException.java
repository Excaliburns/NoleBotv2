package com.tut.nolebotshared.exceptions;

public class MemberNotFoundException extends SnowflakeNotFoundException {
    public MemberNotFoundException(final String errorMessage) { super(errorMessage); }
    public MemberNotFoundException(final String errorMessage, Throwable err) { super(errorMessage, err); }
}
