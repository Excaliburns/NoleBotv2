package com.tut.nolebotv2webapi.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;

import java.util.HashMap;

@Controller("/oauth")
public class AuthController {
    @Post("/discord")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<HashMap<String, String>> discord(
            @Body final String clientCode
    ) {


        return HttpResponse.ok(hashMap);
    }
}
