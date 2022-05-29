package wooteco.subway.dao.section;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.section.Section;

public interface SectionDao {

    Section save(Section section);

    void deleteById(Long id);

    Optional<Section> findById(Long id);

    List<Section> findSectionsByLineId(Long lineId);

    void deleteByLineId(Long lineId);

    void update(Section updated);

    List<Section> findAll();

    Boolean existStation(Long id);
}
