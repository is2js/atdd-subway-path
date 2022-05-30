package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.testutils.SubWayFixtures.강남역;
import static wooteco.subway.testutils.SubWayFixtures.선릉역;
import static wooteco.subway.testutils.SubWayFixtures.잠실역;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.line.JdbcLineDao;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dao.section.JdbcSectionDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.exception.LineDuplicateException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.ui.dto.request.LineRequest;

@JdbcTest
class LineServiceTest {

    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        this.lineDao = new JdbcLineDao(dataSource);
        this.sectionDao = new JdbcSectionDao(dataSource);
        this.stationDao = new JdbcStationDao(dataSource);
        this.lineService = new LineService(lineDao, sectionDao, stationDao);
    }

    @DisplayName("새로운 호선을 생성한다")
    @Test
    void create() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);

        final Line line = lineService.create(new LineRequest("신분당선", "bg-red-600", 일번역.getId(), 이번역.getId(), 10));

        //when & then
        assertThat(line.getId()).isNotNull();

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
    }

    @DisplayName("호선을 중복 생성하면 예외가 발생한다.")
    @Test
    void create_duplicate() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);

        lineService.create(new LineRequest("신분당선", "bg-red-600", 일번역.getId(), 이번역.getId(), 10));

        //when & then
        assertThatThrownBy(
            () -> lineService.create(new LineRequest("신분당선", "bg-red-600", 일번역.getId(), 이번역.getId(), 10)))
            .isInstanceOf(LineDuplicateException.class)
            .hasMessage("[ERROR] 이미 존재하는 노선입니다.");

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
    }


    @DisplayName("모든 호선들을 조회할 수 있다.")
    @Test
    void findAll() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);

        lineService.create(new LineRequest("신분당선", "bg-red-600", 일번역.getId(), 이번역.getId(), 10));
        lineService.create(new LineRequest("분당선", "bg-red-600", 일번역.getId(), 삼번역.getId(), 12));

        //then
        final List<Line> lines = lineService.findAll();

        //when
        assertThat(lines).hasSize(2);

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());
    }

    @DisplayName("특정 노선을 조회할 수 있다.")
    @Test
    void findById() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 삼번역 = stationDao.save(잠실역);

        final Line expected = lineService.create(new LineRequest("분당선", "bg-red-600", 일번역.getId(), 삼번역.getId(), 12));

        //when
        final Line actual = lineService.findById(expected.getId());

        //then
        assertThat(actual.getId()).isEqualTo(expected.getId());

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(삼번역.getId());
    }

    @DisplayName("특정 노선을 조회시, 없는 노선을 조회 요청하면 예외를 발생시킨다.")
    @Test
    void findById_fail() {
        //given & when &then
        assertThatThrownBy(() -> lineService.findById(-1L))
            .isInstanceOf(LineNotFoundException.class)
            .hasMessage("[ERROR] 해당 노선이 없습니다.");
    }

    @DisplayName("특정 노선을 수정할 수 있다.")
    @Test
    void update() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);

        final Line line = lineService.create(new LineRequest("신분당선", "bg-red-600", 일번역.getId(), 이번역.getId(), 10));
        final LineRequest lineRequest = new LineRequest("돌범선", "WHITE");

        //when
        lineService.update(line.getId(), lineRequest);

        //then
        assertThat(lineService.findById(line.getId()).getName()).isEqualTo("돌범선");

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
    }

    @DisplayName("특정 노선을 수정시, 없는 노선을 수정 요청하면 예외를 발생시킨다.")
    @Test
    void update_fail_invalid_id() {
        //given
        final LineRequest lineRequest = new LineRequest("돌범선", "WHITE");

        //when & then
        assertThatThrownBy(() -> lineService.update(-1L, lineRequest))
            .isInstanceOf(LineNotFoundException.class)
            .hasMessage("[ERROR] 해당 노선이 없습니다.");
    }

    @DisplayName("특정 노선을 수정시, 이미 존재하는 이름으로 수정 요청하면 예외를 발생시킨다.")
    @Test
    void update_fail_duplicate_id() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);

        final Line create1 = lineService.create(new LineRequest("중앙선", "bg-red-600", 일번역.getId(), 삼번역.getId(), 12));
        final Line create2 = lineService.create(new LineRequest("2호선", "bg-red-601", 일번역.getId(), 삼번역.getId(), 12));
        final LineRequest lineRequest = new LineRequest(create2.getName(), create2.getColor());

        //when & then
        assertThatThrownBy(() -> lineService.update(create1.getId(), lineRequest))
            .isInstanceOf(LineDuplicateException.class)
            .hasMessage("[ERROR] 이미 존재하는 노선입니다.");

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());

        lineDao.deleteById(create1.getId());
        lineDao.deleteById(create2.getId());
    }

    @DisplayName("특정 노선을 제거할 수 있다.")
    @Test
    void delete() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);

        final Line line = lineService.create(new LineRequest("신분당선", "bg-red-600", 일번역.getId(), 이번역.getId(), 10));

        //when
        lineService.delete(line.getId());

        //then
        assertAll(
            () -> assertThatThrownBy(() -> lineService.findById(line.getId()))
                .isInstanceOf(LineNotFoundException.class)
                .hasMessage("[ERROR] 해당 노선이 없습니다."),
            () -> assertThat(sectionDao.findSectionsByLineId(line.getId())).isEmpty()
        );

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());
    }

    @DisplayName("특정 노선을 삭제시, 없는 노선을 삭제 요청하면 예외를 발생시킨다.")
    @Test
    void delete_fail() {
        //given & when &then
        assertThatThrownBy(() -> lineService.delete(-1L))
            .isInstanceOf(LineNotFoundException.class)
            .hasMessage("[ERROR] 해당 노선이 없습니다.");
    }

    @DisplayName("지하철 노선 생성시, 상/하행종점을 바탕으로 기본 구간을 추가로 만든다.")
    @Test
    void create_with_default_section() {
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);

        final LineRequest lineRequest = new LineRequest("1호선", "green", 일번역.getId(), 이번역.getId(), 10);

        final Line line = lineService.create(lineRequest);
        final Section expected = new Section(line.getId(), 일번역, 이번역, 10);
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId());

        assertThat(sections).usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(List.of(expected));

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());

        lineDao.deleteById(line.getId());
        sectionDao.deleteByLineId(line.getId());
    }
}
