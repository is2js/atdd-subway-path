package wooteco.subway.ui.dto.request;

import java.util.Objects;

public class PathRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long source;
    private Long target;
    private int age;

    private PathRequest() {
    }

    public PathRequest(final Long source, final Long target, final int age) {
        this.source = Objects.requireNonNull(source, ERROR_NULL);
        this.target = Objects.requireNonNull(target, ERROR_NULL);
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
