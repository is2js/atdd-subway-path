package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.testutils.SubWayFixtures.강남역;
import static wooteco.subway.testutils.SubWayFixtures.사당역;
import static wooteco.subway.testutils.SubWayFixtures.선릉역;
import static wooteco.subway.testutils.SubWayFixtures.신림역;
import static wooteco.subway.testutils.SubWayFixtures.잠실역;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.section.JdbcSectionDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.path.Path;
import wooteco.subway.domain.section.Section;
import wooteco.subway.ui.dto.request.PathRequest;

@JdbcTest
class PathServiceTest {

    @Autowired
    private DataSource dataSource;

    private PathService pathService;
    private StationDao stationDao;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        this.stationDao = new JdbcStationDao(dataSource);
        this.sectionDao = new JdbcSectionDao(dataSource);
        this.pathService = new PathService(this.sectionDao, this.stationDao);
    }

    @DisplayName("")
    @Test
    void show() {
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);
        final Station 사번역 = stationDao.save(사당역);
        final Station 오번역 = stationDao.save(신림역);

        final List<Section> sections = List.of(
            new Section(1L, 일번역, 이번역, 10),
            new Section(2L, 이번역, 삼번역, 12),
            new Section(1L, 삼번역, 사번역, 5),
            new Section(1L, 사번역, 오번역, 3),
            new Section(1L, 일번역, 오번역, 31)
        );

        for (final Section section : sections) {
            sectionDao.save(section);
        }

        final Path expected = new Path(List.of(일번역, 이번역, 삼번역, 사번역, 오번역), 30);

        final PathRequest pathRequest = new PathRequest(일번역.getId(), 오번역.getId(), 15);

        //when
        final Path actual = pathService.show(pathRequest);

        //then
        assertThat(actual.toString()).isEqualTo(expected.toString());

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());
        stationDao.deleteById(사번역.getId());
        stationDao.deleteById(오번역.getId());
    }
}
