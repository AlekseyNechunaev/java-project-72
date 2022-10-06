package hexlet.code.controllers;

import hexlet.code.constants.Path;
import hexlet.code.domain.Url;
import hexlet.code.repository.UrlRepositoryImpl;
import io.javalin.http.Handler;
import io.javalin.http.HttpCode;
import org.apache.commons.validator.routines.UrlValidator;

public final class UrlController {

    private static UrlRepositoryImpl urlRepository = new UrlRepositoryImpl();

    public static Handler createUrl = ctx -> {
        String urlName = ctx.formParamAsClass("url", String.class).getOrDefault(null);
        UrlValidator validator = new UrlValidator();
        if (!validator.isValid(urlName)) {
            ctx.status(HttpCode.BAD_REQUEST);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(Path.WELCOME);
            return;
        }
        int countCurrentUrlInBase = urlRepository.getUrlCountByName(urlName);
        if (countCurrentUrlInBase > 0) {
            ctx.status(HttpCode.CONFLICT);
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "warning");
            ctx.redirect(Path.WELCOME);
            return;
        }
        Url url = new Url(urlName);
        urlRepository.createUrl(url);
        ctx.sessionAttribute("flash", "Страница успешно сохранена");
        ctx.sessionAttribute("flashType", "success");
        ctx.redirect(Path.WELCOME);
    };
}
