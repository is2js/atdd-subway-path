package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.path.JgraphtShortestPathFinder;
import wooteco.subway.domain.path.Path;
import wooteco.subway.domain.path.ShortestPathFinder;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.ui.dto.request.PathRequest;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;


    public PathService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Path show(final PathRequest pathRequest) {
        final ShortestPathFinder shortestPathFinder = JgraphtShortestPathFinder.of(new Sections(sectionDao.findAll()));

        final Station source = stationDao.findById(pathRequest.getSource())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 찾는 지하철역이 없습니다."));
        final Station target = stationDao.findById(pathRequest.getTarget())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 찾는 지하철역이 없습니다."));

        return shortestPathFinder.find(source, target);
    }
}
