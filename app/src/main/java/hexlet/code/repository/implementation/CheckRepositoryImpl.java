package hexlet.code.repository.implementation;

import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrlCheck;
import hexlet.code.repository.CheckRepository;

import java.util.List;

public class CheckRepositoryImpl implements CheckRepository<UrlCheck, Long> {

    @Override
    public List<UrlCheck> getChecksListByUrlId(Long id) {
        return new QUrlCheck()
                .url.id.eq(id)
                .findList();
    }

    @Override
    public void createCheck(UrlCheck check) {
        check.save();
    }
}
