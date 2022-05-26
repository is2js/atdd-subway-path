package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_2번역;
import static wooteco.subway.testutils.SubWayFixtures.일호선_구간_1번역_3번역;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.section.Section;

@JdbcTest
class JdbcSectionDaoTest {

    @Autowired
    private DataSource dataSource;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(dataSource);
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
        final Section expected = sectionDao.save(일호선_구간_1번역_2번역);

        //when
        final Section actual = sectionDao.findById(expected.getId()).orElseThrow();

        //then
        assertThat(actual).isEqualTo(expected);

        sectionDao.deleteById(expected.getId());
    }

    @DisplayName("특정 노선의 모든 구간을 조회한다.")
    @Test
    void findAllByLineId() {
        //given
        final Long lineId = 1L;
        final Section createdA = sectionDao.save(일호선_구간_1번역_2번역);
        final Section createdB = sectionDao.save(일호선_구간_1번역_3번역);

        //when
        final List<Section> sections = sectionDao.findSectionByLineId(lineId);

        //then
        assertThat(sections).isNotEmpty();

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
        assertThat(sectionDao.findSectionByLineId(lineId)).isEmpty();

        sectionDao.deleteById(createdA.getId());
        sectionDao.deleteById(createdB.getId());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @DisplayName("수정된 구간정보를 일괄 업데이트 한다.")
    @Test
    void batchUpdate() {
        //given
        final Section createdA = sectionDao.save(일호선_구간_1번역_2번역);
        final Section createdB = sectionDao.save(일호선_구간_1번역_3번역);

        final Section updatedA = new Section(createdA.getId(), 1L, 100L, 101L, 100);
        final Section updatedB = new Section(createdB.getId(), 1L, 200L, 201L, 200);

        //when
        sectionDao.batchUpdate(List.of(updatedA, updatedB));

        //then
        assertAll(
            () -> assertThat(sectionDao.findById(createdA.getId()).get().getDistance()).isEqualTo(100),
            () -> assertThat(sectionDao.findById(createdB.getId()).get().getDistance()).isEqualTo(200)
        );
    }
}
