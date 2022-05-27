package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.SubWayFixtures.STATION_REQUEST_잠실역;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.StationDuplicateException;

@JdbcTest
class StationServiceTest {

    @Autowired
    private DataSource dataSource;
    private StationDao stationDao;
    private SectionDao sectionDao;
    private StationService stationService;

    @BeforeEach
    void setUp() {
        this.stationDao = new JdbcStationDao(dataSource);
        this.sectionDao = new JdbcSectionDao(dataSource);
        this.stationService = new StationService(stationDao, sectionDao);
    }

    @DisplayName("새로운 지하철역을 생성한다")
    @Test
    void create() {
        //given
        final Station 강남역 = stationService.create(STATION_REQUEST_강남역);

        // when & then
        assertThat(강남역.getId()).isNotNull();

        stationService.delete(강남역.getId());
    }

    @DisplayName("호선을 중복 생성하면 예외가 발생한다.")
    @Test
    void create_duplicate() {
        //given
        stationService.create(STATION_REQUEST_강남역);

        //when & then
        assertThatThrownBy(() -> stationService.create(STATION_REQUEST_강남역))
            .isInstanceOf(StationDuplicateException.class)
            .hasMessage("[ERROR] 이미 존재하는 지하철역 이름입니다.");
    }

    @DisplayName("모든 역을 조회한다")
    @Test
    void show() {
        //given
        final Station 강남역 = stationService.create(STATION_REQUEST_강남역);
        final Station 잠실역 = stationService.create(STATION_REQUEST_잠실역);

        //when
        final List<Station> stations = stationService.show();

        //then
        assertThat(stations).hasSize(2);

        stationService.delete(강남역.getId());
        stationService.delete(잠실역.getId());
    }

    @DisplayName("특정 역을 삭제한다")
    @Test
    void delete() {
        //given
        final Station station = stationService.create(STATION_REQUEST_강남역);

        //when
        stationService.delete(station.getId());

        //then
        assertThat(stationService.show()).isEmpty();
    }

    @DisplayName("특정 역을 삭제시, 해당 역이 구간에 사용중이라면 예외를 발생시킨다.")
    @Test
    void delete_fail() {
        //given
        final Station 분당_1역 = stationDao.save(new Station("분당_1역"));
        final Station 분당_2역 = stationDao.save(new Station("분당_2역"));
        final Station 분당_3역 = stationDao.save(new Station("분당_3역"));
        final Section 구간1_2 = sectionDao.save(new Section(1L, 분당_1역.getId(), 분당_2역.getId(), 10));
        final Section 구간2_3 = sectionDao.save(new Section(1L, 분당_2역.getId(), 분당_3역.getId(), 10));

        //when & then
        assertThatThrownBy(() -> stationService.delete(분당_2역.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 해당역은 구간에서 사용되고 있습니다.");

//        stationService.delete(분당_1역.getId());
//        stationService.delete(분당_2역.getId());
//        stationService.delete(분당_3역.getId());
        sectionDao.deleteById(구간1_2.getId());
        sectionDao.deleteById(구간2_3.getId());
    }

    @DisplayName("id List로 전체 역을 조회한다.")
    @Test
    void findByIds() {
        final Station 강남역 = stationService.create(STATION_REQUEST_강남역);
        final Station 잠실역 = stationService.create(STATION_REQUEST_잠실역);
        final List<Long> ids = List.of(강남역.getId(), 잠실역.getId());

        final List<Station> actual = stationService.findByIds(ids);

        assertAll(
            () -> assertThat(actual).hasSize(2),
            () -> assertThat(actual).contains(강남역, 잠실역)
        );

        stationService.delete(강남역.getId());
        stationService.delete(잠실역.getId());
    }
}
