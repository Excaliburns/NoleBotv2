package com.tut.nolebotv2webapi.db.rolecategories;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.SQL_SERVER)
@Join(value = "owners")
@Join(value = "roleIds")
public interface CategoryRepository extends CrudRepository<Category, UUID> {
    String GET_ROLES_BY_OWNER_ID_AND_GUILD_ID = //language=sql
            "SELECT [RoleID],                                               " +
            "       [RoleName],                                             " +
            "       gc.[Id],                                                " +
            "       cr.[id]                                                 " +
            "  FROM GuildCategories gc                                      " +
            "   INNER JOIN CategoryOwners co ON gc.[Id] = co.[CategoryId]   " +
            "   INNER JOIN CategoryRoles cr ON gc.[Id] = cr.[CategoryID]    " +
            "       WHERE OwnerID=:ownerId                                  " +
            "   AND GuildID=:guildId                                        ";

    String GET_ROLE_IDS_BY_OWNER_ID_AND_GUILD_ID = //language=sql
            "SELECT [RoleID]                                              " +
            "  FROM GuildCategories gc                                    " +
            "   INNER JOIN CategoryOwners co ON gc.[Id] = co.[CategoryID] " +
            "   INNER JOIN CategoryRoles cr ON gc.[Id] = cr.[CategoryID]  " +
            "       WHERE OwnerID = :ownerId                              " +
            "AND GuildID = :guildId                                       ";

    @Query(value = GET_ROLES_BY_OWNER_ID_AND_GUILD_ID, nativeQuery = true)
    List<Role> getRolesByOwnerIdAAndGuildId(String ownerId, String guildId);

    @Query(value = GET_ROLE_IDS_BY_OWNER_ID_AND_GUILD_ID, nativeQuery = true)
    List<UUID> getRoleIdsByOwnerIdAndGuildId(String ownerId, String guildId);
}
