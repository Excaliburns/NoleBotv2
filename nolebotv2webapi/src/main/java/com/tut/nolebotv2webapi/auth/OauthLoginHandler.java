package com.tut.nolebotv2webapi.auth;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.handlers.LoginHandler;

// This class is for testing only
public class OauthLoginHandler implements LoginHandler {
    @Override
    public MutableHttpResponse<?> loginSuccess(Authentication authentication, HttpRequest<?> request) {
        return HttpResponse.ok().body("Success").header("Set-Cookie", "test");
    }

    @Override
    public MutableHttpResponse<?> loginRefresh(
            Authentication authentication,
            String refreshToken,
            HttpRequest<?> request
    ) {
        return HttpResponse.ok().body("Success");
    }

    @Override
    public MutableHttpResponse<?> loginFailed(AuthenticationResponse authenticationResponse, HttpRequest<?> request) {
        return HttpResponse.ok().body("Failure");
    }
}
