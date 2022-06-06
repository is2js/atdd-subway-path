package wooteco.subway.domain.fare;

import java.util.Arrays;
import java.util.function.IntPredicate;

public enum AgeDiscountPolicy {

    BABY(age -> 0 < age && age < 6, 0, 0),
    CHILD(age -> 6 <= age && age < 13, 350, 0.5),
    ADOLESCENT(age -> 13 <= age && age < 19, 350, 0.2),
    ADULT(age -> 19 <= age && age <= 150, 0, 0),
    ;

    private final IntPredicate condition;
    private final int discountAmount;
    private final double discountRate;

    AgeDiscountPolicy(final IntPredicate condition, final int discountAmount, final double discountRate) {
        this.condition = condition;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
    }

    static AgeDiscountPolicy from(final int age) {
        return Arrays.stream(values())
            .filter(it -> it.condition.test(age))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 나이는 1~150살 사이만 입력 가능합니다."));
    }

    public int apply(final double value) {
        return (int) ((value - discountAmount) * (1 - discountRate));
    }
}
