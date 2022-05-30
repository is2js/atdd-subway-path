package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.StationDuplicateException;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.ui.dto.request.StationRequest;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(final StationDao stationDao, final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Station create(final StationRequest stationRequest) {
        final Station station = stationRequest.toEntity();
        checkDuplicateName(station);
        return stationDao.save(station);
    }

    private void checkDuplicateName(final Station station) {
        if (stationDao.existsName(station)) {
            throw new StationDuplicateException("[ERROR] 이미 존재하는 지하철역 이름입니다.");
        }
    }

    public List<Station> show() {
        return stationDao.findAll();
    }

    @Transactional
    public void delete(final Long id) {
        stationDao.findById(id)
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));

        if (Boolean.TRUE.equals(sectionDao.existStation(id))) {
            throw new IllegalArgumentException("[ERROR] 해당역은 구간에서 사용되고 있습니다.");
        }

        stationDao.deleteById(id);
    }

    public List<Station> findByIds(final List<Long> ids) {
        return stationDao.findByIds(ids);
    }
}
