package hexlet.code.repository;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;

import java.util.List;
import java.util.Optional;

public class UrlRepositoryImpl implements UrlRepository<Url, Long> {

    @Override
    public void createUrl(Url url) {
        url.save();
    }

    @Override
    public int getUrlCountByName(String name) {
        return new QUrl()
                .name.eq(name)
                .findCount();
    }

    @Override
    public Optional<Url> getUrlById(Long id) {
        return new QUrl()
                .id.eq(id)
                .findOneOrEmpty();
    }

    @Override
    public List<Url> getPagedUrls(int limit, int offset) {
        return new QUrl()
                .findList();
    }
}