package hexlet.code.repository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository<T, R> {

    void createUrl(T url);

    int getUrlCountByName(String name);

    Optional<T> getUrlById(R id);

    List<T> getPagedUrls(int limit, int offset);
}
