package com.tut.nolebotv2webapi.exception;

import io.micronaut.context.annotation.Value;
import io.micronaut.data.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@MappedEntity
@AllArgsConstructor
@Setter
@Getter
public class TestObj {
    @Id
    @MappedProperty(value = "Id")
    private UUID id;
    @MappedProperty(value = "Title")
    private String title;
    public TestObj() {
        id = UUID.randomUUID();
    }
}
