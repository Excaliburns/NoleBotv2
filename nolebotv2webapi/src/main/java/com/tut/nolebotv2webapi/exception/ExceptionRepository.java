package com.tut.nolebotv2webapi.exception;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface ExceptionRepository extends CrudRepository<NoleBotExceptionWrapper, String> {

}
