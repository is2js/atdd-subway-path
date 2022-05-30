package wooteco.subway.dao.section;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private static final RowMapper<Section> SECTION_ROW_MAPPER = (resultSet, rowNum) -> (
        new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            new Station(resultSet.getLong("up_station_id"), resultSet.getString("up_station_name")),
            new Station(resultSet.getLong("down_station_id"), resultSet.getString("down_station_name")),
            resultSet.getInt("distance")
        )
    );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    public JdbcSectionDao(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Section save(final Section section) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource("line_id", section.getLineId())
            .addValue("up_station_id", section.getUpStation().getId())
            .addValue("down_station_id", section.getDownStation().getId())
            .addValue("distance", section.getDistance());
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section);
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM section WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public Optional<Section> findById(final Long id) {
        final String sql = ""
            + "SELECT s.ID as id, s.LINE_ID AS line_id, "
            + "     ust.ID AS up_station_id, ust.NAME AS up_station_name, "
            + "     dst.ID AS down_station_id, dst.NAME AS down_station_name, "
            + "     s.DISTANCE "
            + "FROM section s"
            + "     LEFT JOIN STATION ust "
            + "     ON s.UP_STATION_ID = ust.ID "
            + "     LEFT JOIN STATION dst "
            + "     ON s.DOWN_STATION_ID = dst.ID "
            + "WHERE s.ID = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, SECTION_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Section> findSectionsByLineId(final Long lineId) {
        final String sql = ""
            + "SELECT s.ID as id, s.LINE_ID AS line_id, "
            + "     ust.ID AS up_station_id, ust.NAME AS up_station_name, "
            + "     dst.ID AS down_station_id, dst.NAME AS down_station_name, "
            + "     s.DISTANCE "
            + "FROM section s"
            + "     LEFT JOIN STATION ust "
            + "     ON s.UP_STATION_ID = ust.ID "
            + "     LEFT JOIN STATION dst "
            + "     ON s.DOWN_STATION_ID = dst.ID "
            + "WHERE s.LINE_ID = :lineId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        return namedParameterJdbcTemplate.query(sql, parameters, SECTION_ROW_MAPPER);
    }

    @Override
    public void deleteByLineId(final Long lineId) {
        final String sql = "DELETE FROM section WHERE line_id = :lineId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void update(final Section section) {
        final String sql = ""
            + "UPDATE "
            + "     section "
            + "SET "
            + "     up_station_id = :upStationId, "
            + "     down_station_id = :downStationId, "
            + "     distance = :distance "
            + "WHERE "
            + "     id = :id";

        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", section.getId())
            .addValue("upStationId", section.getUpStation().getId())
            .addValue("downStationId", section.getDownStation().getId())
            .addValue("distance", section.getDistance());
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public List<Section> findAll() {
        final String sql = ""
            + "SELECT s.ID as id, s.LINE_ID AS line_id, "
            + "     ust.ID AS up_station_id, ust.NAME AS up_station_name, "
            + "     dst.ID AS down_station_id, dst.NAME AS down_station_name, "
            + "     s.DISTANCE "
            + "FROM section s"
            + "     LEFT JOIN STATION ust "
            + "     ON s.UP_STATION_ID = ust.ID "
            + "     LEFT JOIN STATION dst "
            + "     ON s.DOWN_STATION_ID = dst.ID ";

        return namedParameterJdbcTemplate.query(sql, SECTION_ROW_MAPPER);
    }

    @Override
    public Boolean existStation(final Long id) {
        final String sql = "SELECT EXISTS (SELECT * FROM section WHERE up_station_id =:id OR down_station_id =:id)";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, parameters, Boolean.class);
    }
}
