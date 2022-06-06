package wooteco.subway.ui.dto.request;

import java.util.Objects;
import javax.validation.constraints.NotBlank;
import wooteco.subway.domain.Station;

public class StationRequest {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    @NotBlank(message = "[ERROR] 이름을 입력하세요.")
    private String name;

    private StationRequest() {
    }

    public StationRequest(String name) {
        this.name = Objects.requireNonNull(name, ERROR_NULL);
    }

    public String getName() {
        return name;
    }

    public Station toEntity() {
        return new Station(this.name);
    }
}
