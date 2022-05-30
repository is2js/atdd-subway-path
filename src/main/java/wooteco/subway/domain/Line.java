package wooteco.subway.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;

public class Line {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long id;
    private Name name;
    private String color;
    private Sections sections;


    public Line(final String name, final String color) {
        this(null, name, color, new Sections(Collections.emptyList()));
    }

    public Line(String name, String color, final Sections sections) {
        this(null, name, color, sections);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, new Sections(Collections.emptyList()));
    }

    public Line(Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = new Name(Objects.requireNonNull(name, ERROR_NULL));
        this.color = Objects.requireNonNull(color, ERROR_NULL);
        this.sections = sections;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
            && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", name=" + name +
            ", color='" + color + '\'' +
            ", sections=" + sections +
            '}';
    }
}
