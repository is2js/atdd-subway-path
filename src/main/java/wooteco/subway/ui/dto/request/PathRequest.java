package wooteco.subway.ui.dto.request;

public class PathRequest {
    private Long source;
    private Long target;
    private Integer age;

    private PathRequest() {
    }

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
