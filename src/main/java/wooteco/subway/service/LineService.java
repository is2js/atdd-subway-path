package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.ui.dto.request.LineRequest;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public Line create(final LineRequest lineRequest) {
        final Line targetLine = lineRequest.toEntity();
        checkDuplicateName(targetLine);
        final Line created = lineDao.save(targetLine);

        final Station upStation = stationDao.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));
        final Station downStation = stationDao.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));

        final Section targetSection = new Section(created.getId(),
            upStation,
            downStation,
            lineRequest.getDistance());
        sectionDao.save(targetSection);

        return created;
    }

    private void checkDuplicateName(final Line line) {
        if (lineDao.existsName(line)) {
            throw new LineDuplicateException("[ERROR] 이미 존재하는 노선입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Line> findAll() {
//        final List<Line> lines = lineDao.findAll()
//            .stream()
//            .map(line -> new LineResponse(line, findStationsByLine(line))).collect(Collectors.toList());
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new LineNotFoundException("[ERROR] 해당 노선이 없습니다."));
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        findById(id);
        final Line updatedLine = lineRequest.toEntity(id);
        checkDuplicateName(updatedLine);
        lineDao.update(updatedLine);
    }

    @Transactional
    public void delete(final Long id) {
        final Line targetLine = findById(id);

        sectionDao.deleteByLineId(id);

        lineDao.deleteById(targetLine.getId());
    }
}
