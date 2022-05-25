package wooteco.subway.dto.request;

public class PathRequest {
    private final Long source;
    private final Long target;
    private final Integer age;

    public PathRequest(final Long source, final Long target, final Integer age) {
        this.source = source;
        this.target = target;
        this.age = age;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "PathRequest{" +
            "source=" + source +
            ", target=" + target +
            ", age=" + age +
            '}';
    }
}
