package com.tut.nolebotshared.entities;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@MappedEntity(value = "CategoryOwners")
@Getter
@Setter
@AllArgsConstructor
@Introspected
public class Owner {
    @MappedProperty("Id")
    @Id
    @GeneratedValue
    private UUID id;

    @MappedProperty("CategoryId")
    private UUID categoryId;

    @MappedProperty("OwnerId")
    private String ownerId;

    @MappedProperty("OwnerName")
    private String ownerName;


}
