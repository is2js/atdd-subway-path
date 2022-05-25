package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.path.Path;
import wooteco.subway.domain.path.ShortestPathFinder;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.request.PathRequest;

@Service
public class PathService {
    private final SectionDao sectionDao;


    public PathService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Path show(final PathRequest pathRequest) {
        final Sections sections = new Sections(sectionDao.findAll());
        final List<Long> stationIds = sections.getTotalStationIds();
        final ShortestPathFinder shortestPathFinder = ShortestPathFinder.of(stationIds, sections.getSortedSections());

        return shortestPathFinder.find(pathRequest.getSource(), pathRequest.getTarget());
    }

}
