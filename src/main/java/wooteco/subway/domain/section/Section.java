package wooteco.subway.domain.section;

import java.util.Objects;
import wooteco.subway.domain.Station;

public class Section {

    private static final String ERROR_INVALID_DISTANCE = "[ERROR] 부적절한 거리가 입력되었습니다. 0보다 큰 거리를 입력해주세요.";
    private static final String ERROR_SAME_STATION = "[ERROR] 상행 종점과 하행 종점이 같을 수 없습니다.";
    private static final int INVALID_DISTANCE_STANDARD = 0;

    private final Long id;
    private final long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id, final Section previousSection) {
        this(id, previousSection.lineId, previousSection.upStation, previousSection.downStation,
            previousSection.distance);
    }

    public Section(final long lineId, final Station upStation, final Station downStation, final int distance) {
        this(null, lineId, upStation, downStation, distance);
    }

    public Section(final Long id, final long lineId, final Station upStation, final Station downStation,
                   final int distance) {
        validateSameStations(upStation, downStation);
        validateInValidDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSameStations(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(ERROR_SAME_STATION);
        }
    }

    private void validateInValidDistance(final int distance) {
        if (distance <= INVALID_DISTANCE_STANDARD) {
            throw new IllegalArgumentException(ERROR_INVALID_DISTANCE);
        }
    }

    public Section createMiddleToDownSection(final Section unSplitSection) {
        return new Section(unSplitSection.id,
            lineId,
            downStation,
            unSplitSection.downStation,
            unSplitSection.getDistance() - distance);
    }

    public Section createUpToMiddleSection(final Section unSplitSection) {
        return new Section(unSplitSection.id, lineId, unSplitSection.upStation, upStation,
            unSplitSection.getDistance() - distance);
    }

    public Section createUpToDownSection(final Section middleToDownSection) {
        return new Section(id, lineId, upStation, middleToDownSection.downStation,
            distance + middleToDownSection.distance);
    }

    public boolean isOnlyUpStationSame(final Section section) {
        return Objects.equals(section.upStation, upStation) &&
            !Objects.equals(section.downStation, downStation);
    }

    public boolean isOnlyDownStationSame(final Section section) {
        return !Objects.equals(section.upStation, upStation) &&
            Objects.equals(section.downStation, downStation);
    }

    public boolean addNewUpStationCase(final Section section) {
        return Objects.equals(section.downStation, upStation);
    }

    public boolean addNewDownStationCase(final Section section) {
        return Objects.equals(section.upStation, downStation);
    }

    public boolean isSameUpStation(final Section section) {
        return Objects.equals(upStation, section.upStation);
    }

    public boolean isSameDownStation(final Section section) {
        return Objects.equals(downStation, section.downStation);
    }

    public boolean isConnected(final Section section) {
        return Objects.equals(downStation, section.upStation);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public boolean isSameOppositeStation(final Section section) {
        return Objects.equals(downStation, section.upStation);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return getLineId() == section.getLineId() && getDistance() == section.getDistance() && Objects.equals(
            getId(), section.getId()) && Objects.equals(upStation, section.upStation) && Objects.equals(
            downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLineId(), upStation, downStation, getDistance());
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", lineId=" + lineId +
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", distance=" + distance +
            '}';
    }
}
