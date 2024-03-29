package com.tut.nolebotv2webapi.db.rolecategories;

import com.tut.nolebotshared.entities.Category;
import com.tut.nolebotshared.entities.Role;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.SQL_SERVER)
public interface CategoryRepository extends CrudRepository<Category, UUID> {
    String GET_ROLES_BY_OWNER_ID_AND_GUILD_ID = //language=sql
            "SELECT [RoleId],                                               " +
            "       [RoleName],                                             " +
            "       gc.[Id] as CategoryId,                                  " +
            "       cr.[Id] as Id                                           " +
            "  FROM GuildCategories gc                                      " +
            "   INNER JOIN CategoryOwners co ON gc.[Id] = co.[CategoryId]   " +
            "   INNER JOIN CategoryRoles cr ON gc.[Id] = cr.[CategoryId]    " +
            "       WHERE OwnerId=:ownerId                                  " +
            "   AND GuildId=:guildId                                        ";

    String GET_ROLE_IDS_BY_OWNER_ID_AND_GUILD_ID = //language=sql
            "SELECT [RoleID]                                              " +
            "  FROM GuildCategories gc                                    " +
            "   INNER JOIN CategoryOwners co ON gc.[Id] = co.[CategoryId] " +
            "   INNER JOIN CategoryRoles cr ON gc.[Id] = cr.[CategoryId]  " +
            "WHERE OwnerId = :ownerId                                     " +
            "AND GuildId = :guildId                                       ";

    @Join(value = "owners", type = Join.Type.INNER)
    @Join(value = "roles", type = Join.Type.INNER)
    Set<Role> findRolesByGuildIdAndOwnersOwnerId(String guildId, String userId);

    @Join(value = "owners", type = Join.Type.INNER)
    @Join(value = "roles", type = Join.Type.INNER)
    Set<String> findRolesRoleIdByGuildIdAndOwnersOwnerId(String guildId, String ownerId);

    @Join(value = "roles", type = Join.Type.LEFT_FETCH)
    @Join(value = "owners", type = Join.Type.LEFT_FETCH)
    Set<Category> getByGuildId(String guildId);
}
