package hexlet.code.controllers;

import hexlet.code.constants.FlashType;
import hexlet.code.constants.Message;
import hexlet.code.constants.Path;
import hexlet.code.domain.Url;
import hexlet.code.repository.UrlRepositoryImpl;
import io.javalin.http.Handler;
import io.javalin.http.HttpCode;

import java.net.URL;

public final class UrlController {

    private static UrlRepositoryImpl urlRepository = new UrlRepositoryImpl();

    public static Handler createUrl = ctx -> {
        String urlName = ctx.formParamAsClass("url", String.class).getOrDefault(null);
        Url url = new Url(urlName);
        if (!url.isValid()) {
            ctx.status(HttpCode.BAD_REQUEST);
            ctx.sessionAttribute("flash", Message.INCORRECT_URL);
            ctx.sessionAttribute("flashType", FlashType.DANGER);
            ctx.redirect(Path.WELCOME);
            return;
        }
        String urlHost = new URL(url.getName()).getHost();
        if (urlRepository.isExistsUrl(urlHost)) {
            ctx.status(HttpCode.CONFLICT);
            ctx.sessionAttribute("flash", Message.URL_ALREADY_EXISTS);
            ctx.sessionAttribute("flashType", FlashType.WARNING);
            ctx.redirect(Path.WELCOME);
            return;
        }
        url.toUrlFormat();
        urlRepository.createUrl(url);
        ctx.sessionAttribute("flash", Message.SUCCESS);
        ctx.sessionAttribute("flashType", FlashType.SUCCESS);
        ctx.redirect(Path.WELCOME);
    };

}
