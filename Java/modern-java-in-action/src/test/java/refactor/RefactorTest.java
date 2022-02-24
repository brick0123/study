package refactor;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import stream.Dish;
import stream.Dish.Type;
import stream.GroupingTest.CaloricLevel;

public class RefactorTest {

    List<Dish> dishes = List.of(
        new Dish(Type.PIZZA, 10),
        new Dish(Type.PASTA, 20),
        new Dish(Type.CHICKEN, 30),
        new Dish(Type.CHICKEN, 40),
        new Dish(Type.CHICKEN, 50)
    );

    @Test
    void grouping() {
//        final Map<CaloricLevel, List<Dish>> dishByType = dishes.stream()
//            .collect(
//                groupingBy(dish -> {
//                    if (dish.getCalories() <= 10) {
//                        return CaloricLevel.DIET;
//                    }
//                    if (dish.getCalories() <= 30) {
//                        return CaloricLevel.NORMAL;
//                    }
//                    return CaloricLevel.FAT;
//                })
//            );

        final Map<CaloricLevel, List<Dish>> dishByType = dishes.stream()
            .collect(
                groupingBy(Dish::getCaloricType));
    }

    @Test
    void reduce() {
//        final int totalCalories = dishes.stream().mapToInt(Dish::getCalories)
//            .reduce(0, Integer::sum);

        final Integer totalCalories = dishes.stream()
            .collect(summingInt(Dish::getCalories));
    }
}

interface Task {

    void execute();
}

