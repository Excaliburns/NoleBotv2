package com.tut.nolebotv2webapi.db.rolecategories;

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
public class Owner {
    @MappedProperty("Id")
    @Id
    @GeneratedValue
    private UUID id;

    @MappedProperty("CategoryID")
    private String categoryId;

    @MappedProperty("OwnerID")
    private String ownerId;


}
