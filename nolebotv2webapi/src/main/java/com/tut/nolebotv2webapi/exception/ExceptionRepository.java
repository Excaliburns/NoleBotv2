package com.tut.nolebotv2webapi.exception;

import io.micronaut.context.annotation.Executable;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.annotation.RepositoryConfiguration;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.operations.JdbcRepositoryOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.operations.RepositoryOperations;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.PageableRepository;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface ExceptionRepository extends PageableRepository<TestObj, UUID> {
}
