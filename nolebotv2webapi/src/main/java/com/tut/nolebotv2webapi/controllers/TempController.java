package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotv2webapi.exception.ExceptionRepository;
import com.tut.nolebotv2webapi.exception.TestObj;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import lombok.NoArgsConstructor;

import javax.inject.Inject;

@Controller
@NoArgsConstructor
public class TempController {
    @Inject
    private ExceptionRepository repo;
    @Get("/test")
    public HttpResponse<String> addTestObjToDB() {
        TestObj obj = new TestObj();
        obj.setTitle("TestO");
        repo.save(obj);
        return HttpResponse.ok("It worked!");
    }
}
