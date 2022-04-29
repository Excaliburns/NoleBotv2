package com.tut.nolebotv2webapi.exception;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@MappedEntity(value = "NolebotExceptions")
@Slf4j
@Setter
//This is a wrapper class for NoleBotExceptions, JPA entities need to be in webapi for Micronaut to compile them
public class NoleBotExceptionWrapper {
    private String id;
    private Timestamp timestamp;
    private String origClass;
    private int lineNum;
    private String methodName;
    private String rootCause;
    private String message;
    private String user;

    private NoleBotExceptionWrapper() {
        id = UUID.randomUUID().toString();
        timestamp = Timestamp.from(Instant.now());
    }

    @Id
    @MappedProperty(value = "Id")
    public String getId() {
        return id;
    }

    @MappedProperty(value = "Timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @MappedProperty(value = "OrigClass")
    public String getOrigClass() {
        return origClass;
    }

    @MappedProperty(value = "LineNum")
    public int getLineNum() {
        return lineNum;
    }

    @MappedProperty(value = "MethodName")
    public String getMethodName() {
        return methodName;
    }

    @MappedProperty(value = "RootCause")
    public String getRootCause() {
        return rootCause;
    }

    @MappedProperty(value = "Message")
    public String getMessage() {
        return message;
    }

    @MappedProperty(value = "Username")
    public String getUser() {
        return user;
    }

    /**
     * Since exceptions aren't serializable, we use this ExceptionWrapper to save them to our DB.
     *
     * @param ex The exception we are wrapping
     * @return A wrapper for ex
     */
    public static NoleBotExceptionWrapper getWrapperForException(Exception ex) {
        NoleBotExceptionWrapper result = new NoleBotExceptionWrapper();
        StackTraceElement rootCause = ex.getStackTrace()[0];
        result.setLineNum(rootCause.getLineNumber());
        result.setOrigClass(rootCause.getClassName());
        result.setMethodName(rootCause.getMethodName());
        result.setRootCause(rootCause.toString());
        result.setMessage(ex.getMessage());
        return result;
    }

}
