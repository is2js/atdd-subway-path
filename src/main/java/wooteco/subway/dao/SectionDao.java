package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.section.Section;

public interface SectionDao {

    Section save(Section section);

    void deleteById(Long id);

    Optional<Section> findById(Long id);

    List<Section> findSectionByLineId(Long lineId);

    void deleteByLineId(Long lineId);

    void batchUpdate(List<Section> sections);

    void update(Section updated);

    List<Section> findAll();

    Boolean existStation(Long id);
}
