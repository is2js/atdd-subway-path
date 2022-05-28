package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

@Repository
public class JdbcLineDao implements LineDao {

    private static final RowMapper<Line> LINE_ROW_MAPPER = (resultSet, rowNum) ->
        new Line(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("color")
        );

//    private static final RowMapper<Line> LINE_JOIN_SECTION_ROW_MAPPER = (resultSet, rowNum) ->
//        new Line(resultSet.getLong("line_id"),
//            resultSet.getString("line_name"),
//            resultSet.getString("line_color"),
//            new Section(resultSet.getLong("section_id"),
//                resultSet.getLong("section_id"),
//                resultSet.getLong("section_up_station_id"),
//                resultSet.getLong("section_down_station_id"),
//                resultSet.getInt("section_distance"))
//        );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcLineDao(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("line")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public boolean existsName(final Line line) {
        final String sql = "SELECT EXISTS (SELECT 1 FROM LINE WHERE name = :name)";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(sql, parameters, Boolean.class));
    }

    public Line save(final Line line) {
        final SqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        final Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return new Line(id, line.getName(), line.getColor());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Optional<Line> findById(final Long id) {
        final String sql = ""
            + "SELECT "
            + "     l.id AS id, l.name AS name, l.COLOR AS color "
            + "FROM "
            + "     LINE l "
            + "WHERE "
            + "     l.id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, LINE_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Line> findAll() {
        final String sql = ""
            + "SELECT "
            + "    l.ID as id, l.NAME as name, l.COLOR as color "
            + "FROM "
            + "     LINE l";

        return namedParameterJdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    @Override
    public void update(final Line line) {
        final String sql = ""
            + "UPDATE line l "
            + "SET l.name = :name, l.color = :color "
            + "WHERE l.id = :id";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = ""
            + "DELETE "
            + "FROM line "
            + "WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

}
