package hexlet.code.repository;

import java.util.List;

public interface CheckRepository<T, R> {

    List<T> getChecksByUrlId(R id);

    void createCheck(T check);
}
