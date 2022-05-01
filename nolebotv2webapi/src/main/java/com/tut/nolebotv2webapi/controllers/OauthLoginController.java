package com.tut.nolebotv2webapi.controllers;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.Authenticator;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.endpoints.LoginController;
import io.micronaut.security.event.LoginFailedEvent;
import io.micronaut.security.event.LoginSuccessfulEvent;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

@Replaces(LoginController.class)
@Requires(beans = LoginHandler.class)
@Requires(beans = Authenticator.class)
@Controller("/login")
@Secured(SecurityRule.IS_ANONYMOUS)
@Slf4j
public class OauthLoginController {

    private final Authenticator authenticator;
    private final LoginHandler loginHandler;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * A Controller for the /login endpoint, handles all authentication.
     *
     * @param authenticator  {@link Authenticator} collaborator
     * @param loginHandler   A collaborator which helps to build HTTP response depending on success or failure.
     * @param eventPublisher The application event publisher
     */
    public OauthLoginController(
            Authenticator authenticator,
            LoginHandler loginHandler,
            ApplicationEventPublisher eventPublisher
    ) {
        this.authenticator = authenticator;
        this.loginHandler = loginHandler;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Authenticates a user with an authorization code from Discord.
     *
     * @param authToken The authorization code received from Discord's redirect
     * @param request The HTTP request for the login
     */
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON})
    @Post
    @SingleResult
    public Publisher<MutableHttpResponse<?>> login(
            @Body(value = "auth_token") String authToken,
            HttpRequest<?> request
    ) {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("",  authToken);

        // This code is mostly taken from LoginController
        return Flux.from(authenticator.authenticate(request, creds))
                .map(authenticationResponse -> {
                    if (authenticationResponse.isAuthenticated()
                            && authenticationResponse.getAuthentication().isPresent()) {
                        Authentication authentication = authenticationResponse.getAuthentication().get();
                        eventPublisher.publishEvent(new LoginSuccessfulEvent(authentication));
                        return loginHandler.loginSuccess(authentication, request);
                    }
                    else {
                        eventPublisher.publishEvent(new LoginFailedEvent(authenticationResponse));
                        return loginHandler.loginFailed(authenticationResponse, request);
                    }
                }).defaultIfEmpty(HttpResponse.status(HttpStatus.UNAUTHORIZED));
    }

}
