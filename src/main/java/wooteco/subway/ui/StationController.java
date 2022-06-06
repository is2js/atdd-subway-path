package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Station;
import wooteco.subway.service.StationService;
import wooteco.subway.ui.dto.request.StationRequest;
import wooteco.subway.ui.dto.response.StationResponse;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@Valid @RequestBody StationRequest stationRequest) {
        final Station station = stationService.create(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId()))
            .body(new StationResponse(station));
    }

    @GetMapping
    public List<StationResponse> showStations() {
        final List<Station> stations = stationService.show();
        return stations.stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent()
            .build();
    }
}
