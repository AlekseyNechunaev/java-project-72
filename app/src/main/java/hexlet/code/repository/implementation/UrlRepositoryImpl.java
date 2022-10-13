package hexlet.code.repository.implementation;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import hexlet.code.repository.UrlRepository;

import java.util.List;
import java.util.Optional;

public class UrlRepositoryImpl implements UrlRepository<Url, Long> {

    @Override
    public void createUrl(Url url) {
        url.save();
    }


    @Override
    public boolean isExistsUrl(String host) {
        return new QUrl()
                .name.contains(host)
                .exists();
    }

    @Override
    public Optional<Url> getUrlById(Long id) {
        return new QUrl()
                .id.eq(id)
                .findOneOrEmpty();
    }

    @Override
    public List<Url> getPagedUrlsWithChecks(int limit, int offset) {
        QUrl url = QUrl.alias();
        QUrlCheck urlCheck = QUrlCheck.alias();
        return new QUrl()
                .select(url.id, url.name)
                .setFirstRow(offset)
                .setMaxRows(limit)
                .orderBy().id.asc()
                .checks.fetch(urlCheck.statusCode, urlCheck.createdAt)
                .orderBy().checks.createdAt.asc()
                .findPagedList().getList();
    }

    @Override
    public int getCountUrls() {
        return new QUrl()
                .findCount();
    }
}
