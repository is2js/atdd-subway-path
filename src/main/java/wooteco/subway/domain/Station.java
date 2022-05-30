package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private final Long id;
    private final Name name;

    public Station(final String name) {
        this(null, name);
    }

    public Station(final Long id, String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Station station = (Station) o;
        return Objects.equals(getId(), station.getId()) && Objects.equals(getName(), station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Station{" +
            "id=" + id +
            ", name=" + name +
            '}';
    }
}

