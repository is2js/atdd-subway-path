package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.testutils.SubWayFixtures.사번_사당역;
import static wooteco.subway.testutils.SubWayFixtures.삼번_잠실역;
import static wooteco.subway.testutils.SubWayFixtures.오번_신림역;
import static wooteco.subway.testutils.SubWayFixtures.이번_선릉역;
import static wooteco.subway.testutils.SubWayFixtures.일번_강남역;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_2번역_거리_10;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_5번역_거리_31;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_2번역_3번역_거리_12;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_3번역_4번역_거리_5;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_4번역_5번역_거리_3;

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
        this.pathService = new PathService(this.sectionDao);
    }

    @DisplayName("")
    @Test
    void show() {
        final List<Station> stations = List.of(
            일번_강남역,
            이번_선릉역,
            삼번_잠실역,
            사번_사당역,
            오번_신림역
        );
        for (Station station : stations) {
            stationDao.save(station);
        }

        final List<Section> sections = List.of(
            일호선_구간_1번역_2번역_거리_10,
            일호선_구간_2번역_3번역_거리_12,
            일호선_구간_3번역_4번역_거리_5,
            일호선_구간_4번역_5번역_거리_3,
            일호선_구간_1번역_5번역_거리_31
        );
        for (final Section section : sections) {
            sectionDao.save(section);
        }

        final Path expected = new Path(List.of(1L, 2L, 3L, 4L, 5L), 30);

        final PathRequest pathRequest = new PathRequest(1L, 5L, 15);

        //when
        final Path actual = pathService.show(pathRequest);

        //then
        assertThat(actual.toString()).isEqualTo(expected.toString());
    }
}
