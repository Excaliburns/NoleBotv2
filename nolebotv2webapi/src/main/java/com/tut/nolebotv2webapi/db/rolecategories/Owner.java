package com.tut.nolebotv2webapi.db.rolecategories;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@MappedEntity(value = "CategoryOwners")
@Getter
@Setter
@AllArgsConstructor
public class Owner {
    @MappedProperty("id")
    @Id
    @GeneratedValue
    private int id;

    @MappedProperty("CategoryID")
    private String categoryId;

    @MappedProperty("OwnerID")
    private String ownerId;


}
