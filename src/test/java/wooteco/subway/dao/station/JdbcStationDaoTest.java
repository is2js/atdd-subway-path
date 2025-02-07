package wooteco.subway.dao.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.testutils.SubWayFixtures.선릉역;
import static wooteco.subway.testutils.SubWayFixtures.일번_강남역;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Station;

@JdbcTest
public class JdbcStationDaoTest {

    @Autowired
    private DataSource dataSource;
    private StationDao stationDao;

    @BeforeEach
    void set() {
        stationDao = new JdbcStationDao(dataSource);
    }

    @Test
    @DisplayName("지하철역을 저장한다.")
    void save() {
        //given
        final Station created = stationDao.save(일번_강남역);

        //when & then
        assertThat(일번_강남역.getId()).isEqualTo(일번_강남역.getId());

        stationDao.deleteById(created.getId());
    }

    @Test
    @DisplayName("중복된 역을 저장할 경우 예외를 발생시킨다.")
    void save_duplicate() {
        //given
        final Station created = stationDao.save(선릉역);

        //when & then
        assertThatThrownBy(() -> stationDao.save(created))
            .isInstanceOf(DuplicateKeyException.class);

        stationDao.deleteById(created.getId());
    }

    @Test
    @DisplayName("모든 지하철 역을 조회한다")
    void findAll() {
        //given
        final Station created_1 = stationDao.save(선릉역);
        final Station created_2 = stationDao.save(일번_강남역);

        //when & then
        assertThat(stationDao.findAll()).hasSize(2);

        stationDao.deleteById(created_1.getId());
        stationDao.deleteById(created_2.getId());
    }

    @Test
    @DisplayName("입력된 id의 지하철 역을 삭제한다")
    void deleteById() {
        //given
        final Station created = stationDao.save(선릉역);

        //when
        stationDao.deleteById(created.getId());

        //then
        assertThat(stationDao.findAll()).isEmpty();

        stationDao.deleteById(created.getId());
    }
}
