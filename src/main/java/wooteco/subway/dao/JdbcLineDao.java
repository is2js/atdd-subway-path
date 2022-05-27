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
        final String sql = "SELECT EXISTS (SELECT * FROM line WHERE name = :name)";
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
        final String sql = "SELECT * FROM line WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, LINE_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

/*    @Override
    public List<Line> findAll2() {
        final String sql = "SELECT * FROM line";
        return namedParameterJdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }*/

    @Override
    public List<Line> findAll() {
        final String sql = "SELECT * FROM line";

//        final String sql = ""
//            + "SELECT * "
//            + "FROM line "
//            + "LEFT JOIN section ON line.id = section.line_id";

        // join에 쓰는 key -> 겹침 -> AS 지정해줘야 rowMapper가 씀.
        // section객체 생성에 필요한 모든 데이터를 다 가져온다. 겹치는 필드는 1개만 해서 rowMapper에서 공통으로 쓴다.
//        final String sql = ""
//            + "SELECT l.id AS line_id, l.name AS line_name, l.color AS line_color, "
//            + "s.id AS section_id, "
//            + "s.up_station_id AS section_up_station_id, s.down_station_id AS section_down_station_id, "
//            + "s.distance AS section_distance "
//            + "FROM LINE AS l "
//            + "LEFT JOIN SECTION AS s ON s.line_id = l.id";
//        return namedParameterJdbcTemplate.query(sql, LINE_JOIN_SECTION_ROW_MAPPER);
        return namedParameterJdbcTemplate.query(sql, LINE_ROW_MAPPER);
    }

    @Override
    public void update(final Line line) {
        final String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "DELETE FROM line WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

}
