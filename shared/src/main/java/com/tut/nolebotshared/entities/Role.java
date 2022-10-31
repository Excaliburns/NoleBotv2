package com.tut.nolebotshared.entities;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@MappedEntity("CategoryRoles")
@Setter
@Getter
@NoArgsConstructor
public class Role {
    @MappedProperty("RoleId")
    private String roleId;

    @MappedProperty("CategoryId")
    private UUID categoryId;

    @MappedProperty("RoleName")
    private String roleName;

    @Id
    @GeneratedValue
    @MappedProperty("Id")
    private UUID id;
}
