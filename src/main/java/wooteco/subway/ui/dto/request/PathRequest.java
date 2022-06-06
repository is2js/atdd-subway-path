package wooteco.subway.ui.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class PathRequest {

    @NotNull(message = "[ERROR] 출발역을 입력하세요")
    private Long source;
    @NotNull(message = "[ERROR] 도착역을 입력하세요")
    private Long target;
    @Positive(message = "[ERROR] 나이는 양수야 합니다.")
    private int age;

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
