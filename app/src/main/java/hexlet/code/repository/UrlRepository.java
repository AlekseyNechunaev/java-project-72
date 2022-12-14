package hexlet.code.repository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository<T, R> {

    void createUrl(T url);

    boolean isExistsUrl(String host);

    Optional<T> getUrlById(R id);

    List<T> getPagedUrlsWithChecks(int limit, int offset);

    int getCountUrls();
}
