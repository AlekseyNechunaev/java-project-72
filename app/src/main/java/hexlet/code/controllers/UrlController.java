package hexlet.code.controllers;

import hexlet.code.constants.FlashType;
import hexlet.code.constants.Message;
import hexlet.code.constants.Path;
import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.repository.implementation.CheckRepositoryImpl;
import hexlet.code.repository.implementation.UrlRepositoryImpl;
import io.javalin.http.Handler;
import io.javalin.http.HttpCode;
import io.javalin.http.NotFoundResponse;

import java.net.URL;
import java.util.List;

public final class UrlController {

    private static UrlRepositoryImpl urlRepository = new UrlRepositoryImpl();
    private static CheckRepositoryImpl checkRepository = new CheckRepositoryImpl();

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
            ctx.status(HttpCode.BAD_REQUEST);
            ctx.sessionAttribute("flash", Message.URL_ALREADY_EXISTS);
            ctx.sessionAttribute("flashType", FlashType.WARNING);
            ctx.redirect(Path.WELCOME);
            return;
        }
        url.normalize();
        urlRepository.createUrl(url);
        ctx.sessionAttribute("flash", Message.SUCCESS_CREATE_URL);
        ctx.sessionAttribute("flashType", FlashType.SUCCESS);
        ctx.redirect(Path.UrlPath.URLS);
    };

    public static Handler showAll = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int urlsCount = urlRepository.getCountUrls();
        int lastPage = urlsCount % 10 == 0 ? urlsCount / 10 : (urlsCount / 10) + 1;
        int limit = 10;
        int offset = page == 1 ? 0 : (page - 1) * 10;
        List<Url> urls = urlRepository.getPagedUrlsWithChecks(limit, offset);
        ctx.attribute("page", page);
        ctx.attribute("lastPage", lastPage);
        ctx.attribute("urls", urls);
        ctx.render("urls/index.html");
    };

    public static Handler showCurrentUrl = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = urlRepository.getUrlById(id).orElseThrow(NotFoundResponse::new);
        List<UrlCheck> checks = checkRepository.getChecksByUrlId(url.getId());
        ctx.attribute("url", url);
        ctx.attribute("checks", checks);
        ctx.render("urls/show.html");
    };

    public static Handler checkUrl = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = urlRepository.getUrlById(id).orElseThrow(NotFoundResponse::new);
        if (!url.isReachable()) {
            ctx.status(HttpCode.BAD_REQUEST);
            ctx.sessionAttribute("flash", Message.INCORRECT_URL);
            ctx.sessionAttribute("flashType", FlashType.DANGER);
            ctx.redirect(Path.UrlPath.URLS + "/" + id);
            return;
        }
        UrlCheck urlCheck = url.runCheck();
        checkRepository.createCheck(urlCheck);
        ctx.sessionAttribute("flash", Message.SUCCESS_CHECK);
        ctx.sessionAttribute("flashType", FlashType.SUCCESS);
        ctx.redirect(Path.UrlPath.URLS + "/" + id);
    };

}
