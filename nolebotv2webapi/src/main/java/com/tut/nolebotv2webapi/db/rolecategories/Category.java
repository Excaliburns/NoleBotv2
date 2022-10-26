package com.tut.nolebotv2webapi.db.rolecategories;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@MappedEntity("GuildCategories")
@Getter
@Setter
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue
    private UUID id;

    @Nullable
    @Relation(value = Relation.Kind.ONE_TO_MANY, cascade = Relation.Cascade.ALL, mappedBy = "categoryId")
    private List<Owner> owners;

    @Nullable
    @Relation(value = Relation.Kind.ONE_TO_MANY, cascade = Relation.Cascade.ALL, mappedBy = "categoryId")
    private List<Role> roles;

    @MappedProperty("GuildId")
    private String guildId;

    @MappedProperty("CategoryName")
    private String categoryName;

    /**
     * This object represents a category in the DB.
     *
     * @param categoryName The name of the category
     * @param guildId The id of the category
     */
    public Category(String categoryName, String guildId)  {
        this.categoryName = categoryName;
        this.guildId = guildId;
    }

}
