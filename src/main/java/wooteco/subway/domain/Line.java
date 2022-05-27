package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long id;
    private Name name;
    private String color;
//    private Section section;


    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, final String name, final String color) {
        this.id = id;
        this.name = new Name(Objects.requireNonNull(name, ERROR_NULL));
        this.color = Objects.requireNonNull(color, ERROR_NULL);
        // 객체를 필드로 가지며, 인자로 안받을 땐, 기본값을 넣어줘야한다... 일급이라면, null대신 빈list를 빈 인자로서 가지도록 넣어줬을 것이다. new Sections(Collections.emptyList())
        // 포장된 것으로서 null을 가질순 있다.
//        this.section = null;
    }

//    public Line(final Long id, final String name, final String color, final Section section) {
//        this.id = id;
//        this.name = new Name(Objects.requireNonNull(name, ERROR_NULL));
//        this.color = Objects.requireNonNull(color, ERROR_NULL);
//        this.section = section;
//    }

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
