package wooteco.subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.path.Path;
import wooteco.subway.service.PathService;
import wooteco.subway.ui.dto.request.PathRequest;
import wooteco.subway.ui.dto.response.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping()
    public ResponseEntity<PathResponse> showPath(PathRequest pathRequest) {
        final Path path = pathService.show(pathRequest);

        return ResponseEntity.status(HttpStatus.OK)
            .body(PathResponse.from(path.getStations(), path));
    }
}
