package com.tut.nolebotv2webapi.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;


@Singleton
public class WebExceptionHandler implements ExceptionHandler<Exception, HttpResponse<String>> {
    @Inject
    ExceptionRepository repo;

    @Override
    public HttpResponse<String> handle(HttpRequest request, Exception exception) {
        NoleBotExceptionWrapper wrapper = NoleBotExceptionWrapper.getWrapperForException(exception);
        Optional<Object> username = request.getAttribute("discord_user_name");
        username.ifPresent(o -> wrapper.setUser((String) o));
        repo.save(wrapper);

        return HttpResponse.serverError(wrapper.getId());
    }

}
