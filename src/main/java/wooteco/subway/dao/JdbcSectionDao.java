package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.section.Section;

@Repository
public class JdbcSectionDao implements SectionDao {

    private static final RowMapper<Section> SECTION_ROW_MAPPER = (resultSet, rowNum) -> (
        new Section(
            resultSet.getLong("id"),
            resultSet.getLong("line_id"),
            resultSet.getLong("up_station_id"),
            resultSet.getLong("down_station_id"),
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
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("line_id", section.getLineId())
            .addValue("up_station_id", section.getUpStationId())
            .addValue("down_station_id", section.getDownStationId())
            .addValue("distance", section.getDistance());
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
            section.getDistance());
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
        final String sql = "SELECT * FROM section WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, SECTION_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Section> findSectionStationsByLineId(final Long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = :lineId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        return namedParameterJdbcTemplate.query(sql, parameters, SECTION_ROW_MAPPER);
    }

    @Override
    public void deleteAllByLineId(final Long lineId) {
        final String sql = "DELETE FROM section WHERE line_id = :lineId";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("lineId", lineId);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void batchUpdate(final List<Section> sections) {
        final String sql = "UPDATE section SET up_station_id = :upStationId, down_station_id = :downStationId, distance = :distance WHERE id = :id";
        namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(sections));
    }

    @Override
    public void update(final Section section) {
        final String sql = "UPDATE section SET up_station_id = :upStationId, down_station_id = :downStationId, distance = :distance WHERE id = :id";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(section);
        namedParameterJdbcTemplate.update(sql, parameters);
    }
}
