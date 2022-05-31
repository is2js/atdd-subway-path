package wooteco.subway.dao.line;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.exception.LineNotFoundException;

@Repository
public class JdbcLineDao implements LineDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public JdbcLineDao(final DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("line").usingGeneratedKeyColumns("id");
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
        return new Line(id, line.getName(), line.getColor(), line.getExtraFare());
    }

    @Override
    public Optional<Line> findById(final Long id) {
        final String sql = "" + "SELECT "
            + "     l.id AS line_id, l.name AS line_name, l.COLOR AS line_color, l.EXTRA_FARE AS line_extra_fare, "
            + "     s.ID AS section_id, s.DISTANCE AS distance, "
            + "     ust.ID AS up_station_id, ust.NAME AS up_station_name, "
            + "     dst.ID AS down_station_id, dst.NAME AS down_station_name " + "FROM " + "     LINE l "
            + "     LEFT JOIN SECTION s " + "     ON l.ID = s.LINE_ID " + "     LEFT JOIN STATION ust "
            + "     ON s.UP_STATION_ID = ust.ID " + "     LEFT JOIN STATION dst "
            + "     ON s.DOWN_STATION_ID = dst.ID " + "WHERE " + "     l.id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        final List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, parameters);
        return toLine(rows);
    }

    private Optional<Line> toLine(final List<Map<String, Object>> rows) {
        validateNotFoundLine(rows); // 인덱싱 전 list size 검증

        return Optional.of(new Line((Long) rows.get(0).get("line_id"), (String) rows.get(0).get("line_name"),
            (String) rows.get(0).get("line_color"), (int) rows.get(0).get("line_extra_fare"), toSections(rows)));
    }

    private void validateNotFoundLine(final List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            throw new LineNotFoundException("[ERROR] 해당 노선이 없습니다.");
        }
    }

    private Sections toSections(final List<Map<String, Object>> rows) {
        return new Sections(rows.stream().map(this::toSection).collect(Collectors.toList()));
    }

    private Section toSection(final Map<String, Object> row) {
        return new Section((Long) row.get("section_id"), (Long) row.get("line_id"),
            new Station((Long) row.get("up_station_id"), (String) row.get("up_station_name")),
            new Station((Long) row.get("down_station_id"), (String) row.get("down_station_name")),
            (int) row.get("distance"));
    }

    @Override
    public List<Line> findAll() {
        final String sql = ""
            + "SELECT "
            + "     l.ID AS line_id, l.NAME AS line_name, l.COLOR AS line_color, l.EXTRA_FARE AS line_extra_fare, "
            + "     s.ID AS section_id, s.DISTANCE AS distance, "
            + "     ust.ID AS up_station_id, ust.NAME AS up_station_name, "
            + "     dst.ID AS down_station_id, dst.NAME AS down_station_name "
            + "FROM "
            + "     LINE l "
            + "     LEFT JOIN SECTION s "
            + "     ON l.ID = s.LINE_ID "
            + "     LEFT JOIN STATION ust "
            + "     ON s.UP_STATION_ID = ust.ID "
            + "     LEFT JOIN STATION dst "
            + "     ON s.DOWN_STATION_ID = dst.ID ";

        final List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql,
            new MapSqlParameterSource());

        return rows.stream().collect(Collectors.groupingBy(row -> (Long) row.get("line_id"))).values().stream()
            .map(rowsByLine -> toLine(rowsByLine).get()).collect(Collectors.toList());
    }

    @Override
    public void update(final Line line) {
        final String sql = "" + "UPDATE line l " + "SET l.name = :name, l.color = :color, l.EXTRA_FARE = :extraFare "
            + "WHERE l.id = :id";
        final BeanPropertySqlParameterSource parameters = new BeanPropertySqlParameterSource(line);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

    @Override
    public void deleteById(final Long id) {
        final String sql = "" + "DELETE " + "FROM line " + "WHERE id = :id";
        final MapSqlParameterSource parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
    }

}
