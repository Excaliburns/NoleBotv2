package com.tut.nolebotv2webapi.db.rolecategories;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.MYSQL)
@Join("owners")
public interface CategoryRepository extends CrudRepository<Category, String> {
}
