package wooteco.subway.dao.station;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;

public interface StationDao {

    Station save(Station station);

    boolean existsName(Station station);

    List<Station> findAll();

    void deleteById(Long id);

    Optional<Station> findById(Long id);

    List<Station> findByIds(final List<Long> ids);
}
