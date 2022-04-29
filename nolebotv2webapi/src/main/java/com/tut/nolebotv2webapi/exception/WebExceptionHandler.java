package com.tut.nolebotv2webapi.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebExceptionHandler implements ExceptionHandler<Exception, HttpResponse<String>> {
    @Inject
    ExceptionRepository repo;

    @Override
    public HttpResponse<String> handle(HttpRequest request, Exception exception) {
        NoleBotExceptionWrapper wrapper = NoleBotExceptionWrapper.getWrapperForException(exception);
        if (request.getHeaders().contains("X-Username")) {
            wrapper.setUser(request.getHeaders().get("X-Username"));
        }
        repo.save(wrapper);
        return HttpResponse.serverError("<h1> Internal Server Error </h1>" +
                "<p>Please report this error id if you need help: " + wrapper.getId() + "</p>").contentType(MediaType.TEXT_HTML);
    }
}
