package com.tut.nolebotv2webapi.db.exception;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.UUID;

@JdbcRepository(dialect = Dialect.SQL_SERVER)
public interface ExceptionRepository extends CrudRepository<NoleBotExceptionWrapper, UUID> {
}
