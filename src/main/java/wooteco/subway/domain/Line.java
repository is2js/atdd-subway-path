package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long id;
    private Name name;
    private String color;


    public Line(String name,
                String color) {
        this(null, name, color);
    }

    public Line(Long id,
                final String name,
                final String color) {
        Objects.requireNonNull(name, ERROR_NULL);
        Objects.requireNonNull(color, ERROR_NULL);
        this.id = id;
        this.name = new Name(name);
        this.color = color;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Line line = (Line) o;
        return Objects.equals(getId(), line.getId()) && Objects.equals(getName(), line.getName())
            && Objects.equals(getColor(), line.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor());
    }
}
