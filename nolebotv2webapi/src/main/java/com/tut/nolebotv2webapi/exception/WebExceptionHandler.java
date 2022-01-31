package com.tut.nolebotv2webapi.exception;

import com.tut.nolebotshared.exceptions.NoleBotException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebExceptionHandler implements ExceptionHandler<NoleBotException, HttpResponse<String>> {
    @Inject
    ExceptionRepository repo;
    @Override
    public HttpResponse<String> handle(HttpRequest request, NoleBotException exception) {
        NoleBotExceptionWrapper wrapper = NoleBotExceptionWrapper.getWrapperForException(exception);
        repo.save(wrapper);
        return HttpResponse.serverError(wrapper.getId());
    }
}
