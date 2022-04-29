package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.exceptions.NoleBotException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.NoArgsConstructor;

@Controller
@NoArgsConstructor
public class TempController {
    @Get("/test")
    public HttpResponse<String> addTestObjToDB() throws NoleBotException {
        throw new NoleBotException("This is a test error!");
    }
}
