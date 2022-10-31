package com.tut.nolebotv2webapi.controllers;

import com.tut.nolebotshared.entities.Category;
import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketServer;
import com.tut.nolebotv2webapi.db.rolecategories.CategoryRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;

import java.util.Set;

@Log4j2
@Controller("/category")
@Secured(SecurityRule.IS_ANONYMOUS)
public class CategoryController {
    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private CoreWebSocketServer webSocketServer;

    /**
     * Lists categories of a guild.
     *
     * @param guildId The guild id to get categories for
     * @return The set of categories a guild has
     */
    @Get("/{guildId}/list/")
    public HttpResponse<Set<Category>> getCategoryList(@PathVariable String guildId) {
        categoryRepository.getByGuildId(guildId).forEach(category -> {
            log.info(category.getCategoryName());
        });
        return HttpResponse.ok(categoryRepository.getByGuildId(guildId));
    }
}
