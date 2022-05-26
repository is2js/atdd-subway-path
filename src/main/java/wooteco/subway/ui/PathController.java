package wooteco.subway.ui;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.path.Path;
import wooteco.subway.service.PathService;
import wooteco.subway.service.StationService;
import wooteco.subway.ui.dto.request.PathRequest;
import wooteco.subway.ui.dto.response.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;
    private final StationService stationService;

    public PathController(final PathService pathService, final StationService stationService) {
        this.pathService = pathService;
        this.stationService = stationService;
    }

    @GetMapping()
    public ResponseEntity<PathResponse> showPath(PathRequest pathRequest) {
        final Path path = pathService.show(pathRequest);

        final List<Station> stations = stationService.findByIds(path.getStationIds());

        return ResponseEntity.status(HttpStatus.OK)
            .body(PathResponse.from(stations, path));
    }
}
