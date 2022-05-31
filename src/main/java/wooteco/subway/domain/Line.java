package wooteco.subway.domain;

import java.util.Collections;
import java.util.Objects;
import wooteco.subway.domain.section.Sections;

public class Line {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private final Long id;
    private final Name name;
    private final String color;
    private final int extraFare;
    private final Sections sections;


    public Line(final String name, final String color, final int extraFare) {
        this(null, name, color, extraFare, new Sections(Collections.emptyList()));
    }

    public Line(final Long id, final String name, final String color, final int extraFare) {
        this(id, name, color, extraFare, new Sections(Collections.emptyList()));
    }

    public Line(final Long id, final String name, final String color, final int extraFare, final Sections sections) {
        this.id = id;
        this.name = new Name(Objects.requireNonNull(name, ERROR_NULL));
        this.color = Objects.requireNonNull(color, ERROR_NULL);
        this.extraFare = extraFare;
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

    public Sections getSections() {
        return sections;
    }

    public int getExtraFare() {
        return extraFare;
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
        return getExtraFare() == line.getExtraFare() && Objects.equals(getId(), line.getId())
            && Objects.equals(getName(), line.getName()) && Objects.equals(getColor(), line.getColor())
            && Objects.equals(getSections(), line.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), getExtraFare(), getSections());
    }

    @Override
    public String toString() {
        return "Line{" +
            "id=" + id +
            ", name=" + name +
            ", color='" + color + '\'' +
            ", extraFare=" + extraFare +
            ", sections=" + sections +
            '}';
    }
}
