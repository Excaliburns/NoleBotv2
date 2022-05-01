package com.tut.nolebotv2webapi.db.rolecategories;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.MYSQL)
@Join(value = "owners")
@Join(value = "roleIds")
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    @Query(value = "SELECT RoleID,RoleName,GuildCategories.CategoryID,CategoryRoles.id FROM GuildCategories " +
            "INNER JOIN CategoryOwners ON GuildCategories.CategoryID=CategoryOwners.CategoryID " +
            "INNER JOIN CategoryRoles ON GuildCategories.CategoryID=CategoryRoles.CategoryID WHERE OwnerID=:ownerId " +
            "AND GuildID=:guildId",
            nativeQuery = true)
    public List<Role> getRolesByOwnerIdAAndGuildId(String ownerId, String guildId);

    @Query(value = "SELECT RoleID FROM GuildCategories " +
            "INNER JOIN CategoryOwners ON GuildCategories.CategoryID=CategoryOwners.CategoryID " +
            "INNER JOIN CategoryRoles ON GuildCategories.CategoryID=CategoryRoles.CategoryID WHERE OwnerID=:ownerId " +
            "AND GuildID=:guildId",
            nativeQuery = true)
    public List<String> getRoleIdsByOwnerIdAndGuildId(String ownerId, String guildId);
}
