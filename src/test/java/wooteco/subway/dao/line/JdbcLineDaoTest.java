package wooteco.subway.dao.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.testutils.SubWayFixtures.강남역;
import static wooteco.subway.testutils.SubWayFixtures.선릉역;
import static wooteco.subway.testutils.SubWayFixtures.이호선_그린;
import static wooteco.subway.testutils.SubWayFixtures.일호선_파랑;
import static wooteco.subway.testutils.SubWayFixtures.잠실역;

import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.dao.section.JdbcSectionDao;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;

@JdbcTest
public class JdbcLineDaoTest {

    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;


    @BeforeEach
    void set() {
        lineDao = new JdbcLineDao(dataSource);
        stationDao = new JdbcStationDao(dataSource);
        sectionDao = new JdbcSectionDao(dataSource);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);

        final Line 이호선_그린_entity = new Line("2호선", "green", 900);

        //when
        final Line 이호선_그린_domain = lineDao.save(이호선_그린_entity);
        final Section sectionA = sectionDao.save(new Section(이호선_그린_domain, 일번역, 이번역, 10));
        final Section sectionB = sectionDao.save(new Section(이호선_그린_domain, 이번역, 삼번역, 12));

        //then
        Assertions.assertThat(이호선_그린_domain).usingRecursiveComparison()
            .ignoringFields("id") // isEqualTo전에 객체에서 무시할 field를 지정
            .isEqualTo(이호선_그린_entity);

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());
        sectionDao.deleteById(sectionA.getId());
        sectionDao.deleteById(sectionB.getId());

        lineDao.deleteById(이호선_그린_domain.getId());
    }

    @Test
    @DisplayName("중복된 노선을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        //given
        final Line saved = lineDao.save(이호선_그린);

        //when & then
        assertThatThrownBy(() -> lineDao.save(이호선_그린))
            .isInstanceOf(DuplicateKeyException.class);

        lineDao.deleteById(saved.getId());
    }

    @Test
    @DisplayName("모든 노선을 조회한다")
    void findAll() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);

        final Line 일호선 = lineDao.save(일호선_파랑);
        final Line 이호선 = lineDao.save(이호선_그린);

        final Section sectionA = sectionDao.save(new Section(일호선, 일번역, 이번역, 10));
        final Section sectionB = sectionDao.save(new Section(이호선, 이번역, 삼번역, 12));

        //when
        List<Line> lines = lineDao.findAll();

        //then
        assertThat(lines).hasSize(2);

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());
        sectionDao.deleteById(sectionA.getId());
        sectionDao.deleteById(sectionB.getId());
        lineDao.deleteById(일호선.getId());
        lineDao.deleteById(이호선.getId());
    }

    @DisplayName("단일 노선을 조회한다")
    @Test
    void findById() {

        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Line line = lineDao.save(이호선_그린);
        final Section section = sectionDao.save(new Section(line.getId(), line, 일번역, 이번역, 10));

        final Line actual = lineDao.findById(line.getId())
            .orElse(null);

        assertThat(actual.getId()).isNotNull();

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        sectionDao.deleteById(section.getId());
        lineDao.deleteById(line.getId());

    }

    @Test
    @DisplayName("입력된 id의 노선을 삭제한다")
    void deleteById() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);

        final Line created = lineDao.save(이호선_그린);

        final Section sectionA = sectionDao.save(new Section(created, 일번역, 이번역, 10));
        final Section sectionB = sectionDao.save(new Section(created, 이번역, 삼번역, 12));

        //when
        lineDao.deleteById(created.getId());

        //then
        assertThat(lineDao.findAll()).isEmpty();

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());
        sectionDao.deleteById(sectionA.getId());
        sectionDao.deleteById(sectionB.getId());
    }

    @Test
    @DisplayName("입력된 id의 노선을 수정한다.")
    void update() {
        //given
        final Station 일번역 = stationDao.save(강남역);
        final Station 이번역 = stationDao.save(선릉역);
        final Station 삼번역 = stationDao.save(잠실역);

        final Line target = lineDao.save(이호선_그린); // 내부에서 section join시 3번까지... 위에서 미리 생겨서...

        final Section sectionA = sectionDao.save(new Section(target, 일번역, 이번역, 10));
        final Section sectionB = sectionDao.save(new Section(target, 이번역, 삼번역, 12));

        final Line lineForUpdate = new Line(target.getId(), "일호선", "green", 900);

        //when
        lineDao.update(lineForUpdate);
        final Line updatedLine = lineDao.findById(target.getId())
            .orElseThrow();

        //then
        assertThat(updatedLine.getName()).isEqualTo(lineForUpdate.getName());

        stationDao.deleteById(일번역.getId());
        stationDao.deleteById(이번역.getId());
        stationDao.deleteById(삼번역.getId());
        sectionDao.deleteById(sectionA.getId());
        sectionDao.deleteById(sectionB.getId());

        lineDao.deleteById(updatedLine.getId());
    }
}
