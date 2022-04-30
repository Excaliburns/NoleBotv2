package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.exceptions.NoleBotException;
import com.tut.nolebotv2webapi.db.rolecategories.CategoryRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

@Controller
@NoArgsConstructor
@Secured(SecurityRule.IS_ANONYMOUS)
public class TempController {
    @Inject
    private CategoryRepository categoryRepository;

    @Get("/test")
    public HttpResponse<String> addTestObjToDB() throws NoleBotException {
        throw new NoleBotException("This is a test error!");
    }

    /**
     * An endpoint for testing the Role Categories ORM.
     *
     * @return A list of owners matching "temp"
     */
    @Get("/get_categories")
    public HttpResponse<String> getCategories() {
        StringBuilder result = new StringBuilder();
        categoryRepository.findById("temp").ifPresent(category -> {
            category.getOwners().forEach(owner -> {
                result.append(owner.getOwnerId());
            });
        });
        return HttpResponse.ok().body(result.toString());
    }
}
