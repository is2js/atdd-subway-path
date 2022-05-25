package wooteco.subway.domain.path;

import java.util.List;

public class Path {

    private static final int DEFAULT_FARE = 1250;
    private static final int FIRST_ADDITIONAL_FARE_DISTANCE = 10;
    private static final int SECOND_ADDITIONAL_FARE_DISTANCE = 50;
    private final List<Long> stationIds;
    private final int distance;

    public Path(final List<Long> stationIds, final int distance) {
        this.stationIds = stationIds;
        this.distance = distance;
    }

    public int calculateFare() {
        if (distance <= FIRST_ADDITIONAL_FARE_DISTANCE) {
            return DEFAULT_FARE;
        }
        if (distance <= SECOND_ADDITIONAL_FARE_DISTANCE) {
            return DEFAULT_FARE + calculateOverFare(distance - FIRST_ADDITIONAL_FARE_DISTANCE, 5);
        }
        // 2번째 구간까지 가능 distance라면,
        // 1번째 구간 요금 계산시, 51 - 10 = 41이 아니라,  현재값 with maximum 50 - 10 = 로 나와야한다. 50을 넘어가는 순간 2번째 구간에서 계산되어야함.
        //  -> 상한값이 정해진 변수의 경우, min을 하면된다?!
        //  -> 51인데, 상한이 50이다 -> min 
        //  -> 41인데, 상한이 50이다 -> min
        //  -> 상한과 같아질 때 비로소 min이 상한을 택한다.

        // 2번째 구간까지 추가요금이 붙는 경우, 1번째 구간 계산시 this.distance에 위한 상한이 붙는다.
        // -> 조건도 걸리고, 메서드내부에서 인자로 안받고 바로 필드를 쓰는 것도 자제하자. -> 인자에서 조건계산까지 하고 넘겨주자.
        return DEFAULT_FARE
            + calculateOverFare(Math.min(distance, SECOND_ADDITIONAL_FARE_DISTANCE) - FIRST_ADDITIONAL_FARE_DISTANCE, 5)
            + calculateOverFare(distance - SECOND_ADDITIONAL_FARE_DISTANCE, 8);
    }

    private int calculateOverFare(final int distance, final int unitDistance) {
        return (int) (Math.ceil((distance - 1) / unitDistance) + 1) * 100;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Path{" +
            "stationIds=" + stationIds +
            ", distance=" + distance +
            '}';
    }
}
