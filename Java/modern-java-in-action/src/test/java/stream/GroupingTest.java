package stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.filtering;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stream.Dish.Type;

public class GroupingTest {

    List<Dish> dishes = List.of(
        new Dish(Type.PIZZA, 10),
        new Dish(Type.PASTA, 20),
        new Dish(Type.CHICKEN, 30),
        new Dish(Type.CHICKEN, 40),
        new Dish(Type.CHICKEN, 50)
    );

    @Test
    @DisplayName("joining은 내부적으로 StringBuilder를 이용한다")
    void joining() {

    }

    @Test
    @DisplayName("groupingBy(g)는 groupingBy(g, toList()) 의 축약이다.")
    void grouping_1() {
        final Map<Type, List<Dish>> dishesByType = dishes.stream()
            .collect(groupingBy(Dish::getType));

        System.out.println("dishesByType = " + dishesByType);
    }

    @Test
    void grouping_2() {
        // 조건절 filter에다가 걸어버리면, Key 값이 아예 제외될 수도 있다.
        // ex 10칼로리 초과.

        final Map<Type, List<Dish>> collect = dishes.stream()

            .filter(d -> d.getCalories() > 10)
            .collect(groupingBy(Dish::getType));
        System.out.println("collect = " + collect); // pizza는 제외된다.

        final Map<Type, List<Dish>> dishMap = dishes.stream()
            .collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 10, toList())));

        System.out.println("dishMap = " + dishMap);
    }

    @Test
    @DisplayName("grouping mapping")
    void grouping_3() {
        final Map<Type, List<Integer>> dishMap = dishes.stream()
            .collect(groupingBy(Dish::getType, mapping(Dish::getCalories, toList())));

        System.out.println("dishMap = " + dishMap);
    }

    @Test
    void subgroup() {
        final Map<Type, Long> typeCount = dishes.stream()
            .collect(groupingBy(Dish::getType, counting()));
        System.out.println("typeCount = " + typeCount);
    }

    @Test
    void subgroup2() {
        final Map<Type, Integer> typeCount = dishes.stream()
            .collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));
        System.out.println("typeCount = " + typeCount);
    }

    @Test
    void subgroup3() {
        final Map<Type, Set<CaloricLevel>> caloricLevelByType = dishes.stream()
            .collect(groupingBy(Dish::getType, mapping(dish -> {
                        if (dish.getCalories() <= 10) {
                            return CaloricLevel.DIET;
                        }
                        if (dish.getCalories() <= 30) {
                            return CaloricLevel.NORMAL;
                        }
                        return CaloricLevel.FAT;
                    }, toSet()
                ))
            );
        System.out.println("caloricLevelByType = " + caloricLevelByType);
    }

    @Test
    void subgroup3_another_collection() {
        final Map<Type, Set<CaloricLevel>> caloricLevelByType = dishes.stream()
            .collect(groupingBy(Dish::getType, mapping(dish -> {
                        if (dish.getCalories() <= 10) {
                            return CaloricLevel.DIET;
                        }
                        if (dish.getCalories() <= 30) {
                            return CaloricLevel.NORMAL;
                        }
                        return CaloricLevel.FAT;
                    }, toCollection(TreeSet::new)
                ))
            );
        System.out.println("caloricLevelByType = " + caloricLevelByType);
    }


    public enum CaloricLevel {
        FAT, NORMAL, DIET;
    }
}
