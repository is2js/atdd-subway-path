package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.ui.dto.request.SectionRequest;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public List<Station> findStationsByLineId(final Long lineId) {
        final List<Long> stationIds = findStationIdsByLineId(lineId);
        return getSortedStations(stationIds);
    }

    private List<Long> findStationIdsByLineId(final Long lineId) {
        return new Sections(sectionDao.findSectionsByLineId(lineId))
            .getConnectedSections()
            .stream()
            .flatMap(section -> Stream.of(section.getUpStation().getId(), section.getDownStation().getId()))
            .distinct()
            .collect(Collectors.toList());
    }

    private List<Station> getSortedStations(final List<Long> stationIds) {
        return stationDao.findByIds(stationIds)
            .stream()
            .sorted(Comparator.comparing(Station::getId))
            .collect(Collectors.toList());
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final List<Section> currentSections = sectionDao.findSectionsByLineId(lineId);

        final Station upStation = stationDao.findById(sectionRequest.getUpStationId())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));
        final Station downStation = stationDao.findById(sectionRequest.getDownStationId())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));

        final Line line = lineDao.findById(lineId)
            .orElseThrow(() -> new LineNotFoundException("[ERROR] 해당 지하철 호선이 존재하지 않습니다."));

        Section newSection = sectionRequest.toEntity(line, upStation, downStation);
        newSection = sectionDao.save(newSection); // transactional믿고 일단 저장해서 id배정된 domain으로 sections에 진입한다.

        new Sections(currentSections).addSection(newSection)
            .ifPresent(sectionDao::update);
    }

    @Transactional
    public void deleteSection(final Long id, final Long stationId) {
        lineDao.findById(id)
            .orElseThrow(() -> new LineNotFoundException("[ERROR] 해당 노선이 없습니다."));
        stationDao.findById(stationId)
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));

        final Sections sections = new Sections(sectionDao.findSectionsByLineId(id));
        final boolean isMiddleDelete = sections.isMiddleDelete(stationId);
        sectionDao.deleteById(sections.deleteSectionByStationId(stationId));
        if (isMiddleDelete) {
            final Sections updatedSections = new Sections(sectionDao.findSectionsByLineId(id));
            final Section updatedSection = sections.getUpdatedSection(
                updatedSections);
            sectionDao.update(updatedSection);
        }
    }
}
