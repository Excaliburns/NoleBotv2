package com.tut.nolebotv2webapi.db.rolecategories;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedEntity("CategoryRoles")
@Setter
@Getter
@NoArgsConstructor
public class Role {
    @MappedProperty("RoleID")
    private String roleId;

    @MappedProperty("CategoryID")
    private int categoryId;

    @Id
    @GeneratedValue
    @MappedProperty("Id")
    private int id;
}
