package wooteco.subway.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.ui.dto.request.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public List<Station> findSectionStationsByLineId(final Long lineId) {
        final List<Long> stationIds = findSectionStationIds(lineId);
        return getSortedStations(stationIds);
    }

    private List<Long> findSectionStationIds(final Long lineId) {
        return new Sections(sectionDao.findSectionByLineId(lineId))
            .getSortedSections()
            .stream()
            .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
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
        final List<Section> currentSection = sectionDao.findSectionByLineId(lineId);
        Section newSection = sectionRequest.toEntity(lineId);
        newSection = sectionDao.save(newSection);
        new Sections(currentSection).addSection(newSection)
            .ifPresent(sectionDao::update);
    }

    @Transactional
    public void deleteSection(final Long id, final Long stationId) {
        lineDao.findById(id)
            .orElseThrow(() -> new LineNotFoundException("[ERROR] 해당 노선이 없습니다."));
        stationDao.findById(stationId)
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));

        final Sections sections = new Sections(sectionDao.findSectionByLineId(id));
        final boolean isMiddleDelete = sections.isMiddleDelete(stationId); // 구간 삭제 전에, 중간역인지 물어봄
        sectionDao.deleteById(sections.deleteSectionByStationId(stationId)); // 상황에 따라 알아서 역 삭제된 상황으로 구간 삭제되고 이어짐.
        if (isMiddleDelete) { // 구간 삭제전에 물어봤던 중간역여부... (구간 삭제되면 중간역으로 나뉜 구것이 없어진 상태라 못 물어봄)
            final Sections updatedSections = new Sections(sectionDao.findSectionByLineId(id));
            final Section updatedSection = sections.getUpdatedSection(updatedSections); // 기존 구간(sections)에 대해, delete로 업데이트된 구간(updatedSections)을 비교해서,
            // 업데이트해야할 구간1개만 가져온다
            // 기존구간에서  중간역 삭제 상황 1-2-3   ->  1-2  와 2-3 2개 삭제 -> 1개는 id유지 데이터만 변경예정 -> 1개는 실제 삭제(위쪽 코드) -> 1-3은 변경으로 생성 -> update
            sectionDao.update(updatedSection);
        }
    }
}
