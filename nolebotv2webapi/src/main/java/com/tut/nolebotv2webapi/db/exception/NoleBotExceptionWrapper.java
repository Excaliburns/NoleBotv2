package com.tut.nolebotv2webapi.db.exception;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@MappedEntity(value = "NolebotExceptions")
@Slf4j
@Getter
@Setter
@NoArgsConstructor
//This is a wrapper class for NoleBotExceptions, JPA entities need to be in webapi for Micronaut to compile them
public class NoleBotExceptionWrapper {
    @Id
    @MappedProperty(value = "Id")
    @GeneratedValue(value = GeneratedValue.Type.UUID)
    private int id;

    @MappedProperty(value = "Timestamp")
    @DateCreated
    private Instant timestamp;

    @MappedProperty(value = "OrigClass")
    private String origClass;

    @MappedProperty(value = "LineNum")
    private int lineNum;

    @MappedProperty(value = "MethodName")
    private String methodName;

    @MappedProperty(value = "RootCause")
    private String rootCause;

    @MappedProperty(value = "Message")
    private String message;

    @MappedProperty(value = "Username")
    private String user;


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