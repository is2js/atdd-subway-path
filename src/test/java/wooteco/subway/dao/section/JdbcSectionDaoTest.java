package wooteco.subway.dao.section;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.testutils.SubWayFixtures.강남역;
import static wooteco.subway.testutils.SubWayFixtures.선릉역;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_2번역;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_3번역;
import static wooteco.subway.testutils.SubWayFixtures.잠실역;

import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;

@JdbcTest
class JdbcSectionDaoTest {

    @Autowired
    private DataSource dataSource;
    private SectionDao sectionDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(dataSource);
        stationDao = new JdbcStationDao(dataSource);
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void save() {
        //given
        final Section created = sectionDao.save(일호선_구간_1번역_2번역);

        //when
        assertThat(created.getId()).isNotNull();

        //then
        sectionDao.deleteById(created.getId());
    }

    @DisplayName("개별 구간을 조회한다.")
    @Test
    void findById() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);

        final Section expected = sectionDao.save(new Section(1L, 일번역, 이번역, 10));

        //when
        final Section actual = sectionDao.findById(expected.getId())
            .orElseThrow();

        //then
        assertThat(actual).isEqualTo(expected);

        sectionDao.deleteById(expected.getId());

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
    }

    @DisplayName("특정 노선의 모든 구간을 조회한다.")
    @Test
    void findAllByLineId() {
        //given
        final Long lineId = 1L;
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);
        final Section createdA = sectionDao.save(new Section(1L, 일번역, 이번역, 1));
        final Section createdB = sectionDao.save(new Section(1L, 일번역, 삼번역, 2));

        //when
        final List<Section> sections = sectionDao.findSectionsByLineId(lineId);

        //then
        assertThat(sections).isNotEmpty();

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());

        sectionDao.deleteById(createdA.getId());
        sectionDao.deleteById(createdB.getId());
    }

    @DisplayName("특정 노선의 모든 구간을 제거한다.")
    @Test
    void deleteAllByLineId() {
        //given
        final Long lineId = 1L;
        final Section createdA = sectionDao.save(일호선_구간_1번역_2번역);
        final Section createdB = sectionDao.save(일호선_구간_1번역_3번역);

        //when
        sectionDao.deleteByLineId(lineId);

        //then
        assertThat(sectionDao.findSectionsByLineId(lineId)).isEmpty();

        sectionDao.deleteById(createdA.getId());
        sectionDao.deleteById(createdB.getId());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @DisplayName("수정된 구간 정보를 업데이트 한다.")
    @Test
    void update() {
        //given
        final Station 일번역 = stationDao.save(new Station("강남역"));
        final Station 이번역 = stationDao.save(new Station("선릉역"));
        final Station 백번역 = stationDao.save(new Station(100L, "백번역"));
        final Station 백일번역 = stationDao.save(new Station(101L, "백일번역"));

        final Section createdA = sectionDao.save(new Section(1L, 일번역, 이번역, 10));

        final Section updatedA = new Section(createdA.getId(), createdA.getLineId(), 백번역, 백일번역, 100);

        //when
        sectionDao.update(updatedA);

        //then
        assertThat(sectionDao.findById(createdA.getId())
            .get()
            .getDistance()).isEqualTo(100);

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(백번역.getId());
        stationDao.deleteById(백일번역.getId());

        sectionDao.deleteById(createdA.getId());
    }

    @DisplayName("stationId이 주어지면, 구간 중에 해당 지하철역이 존재하는지 확인한다.")
    @ParameterizedTest
    @CsvSource({"1,true", "-1,false"})
    void existStation(final long stationId, final boolean expected) {
        //given
        final Section createdA = sectionDao.save(일호선_구간_1번역_2번역);

        //when
        final Boolean actual = sectionDao.existStation(stationId);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);

        sectionDao.deleteById(createdA.getId());
    }
}
