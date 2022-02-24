package stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toMap;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stream.Dish.Type;

public class PartitionTest {

    List<Dish> dishes = List.of(
        new Dish(Type.PIZZA, true, 10),
        new Dish(Type.PASTA, true, 20),
        new Dish(Type.CHICKEN, false, 30),
        new Dish(Type.CHICKEN, true, 40),
        new Dish(Type.CHICKEN, false, 50)
    );


    @Test
    void partition1() {
        final Map<Boolean, Map<Type, List<Dish>>> vegetarianDishesByType = dishes.stream()
            .collect(groupingBy(Dish::isVegetarian,
                groupingBy(Dish::getType)));

        System.out.println("vegetarianDishesByType = " + vegetarianDishesByType);
    }

    @Test
    @DisplayName("그룹에서 가장 칼로리가 높은 녀석으로")
    void partition2() {
        final Map<Boolean, Dish> mostCaloricPartitionByVegetarian = dishes.stream()
            .collect(partitioningBy(Dish::isVegetarian,
                collectingAndThen(
                    maxBy(Comparator.comparingInt(Dish::getCalories)), Optional::get)
            ));

        final Map<Boolean, Dish> mostCaloricPartitionByVegetarian2 = dishes.stream()
            .collect(toMap(Dish::isVegetarian,
                Function.identity(),
                BinaryOperator.maxBy(Comparator.comparingInt(Dish::getCalories))
            ));

        System.out.println("mostCaloricPartitionByVegetarian2 = " + mostCaloricPartitionByVegetarian2);

        final Map<Boolean, Integer> collect = dishes.stream()
            .collect(toMap(Dish::isVegetarian,
                Dish::getCalories,
                BinaryOperator.maxBy(Comparator.naturalOrder())
            ));
    }

    @Test
    @DisplayName("n을 받아서 2에서 n까지의 자연수를 소수, 비소수로 나누는 프로그램 구현")
    void prime() {
        final Map<Boolean, List<Integer>> booleanListMap = partitionPrimes(10);
        System.out.println("booleanListMap = " + booleanListMap);
    }

    public Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.range(2, n).boxed()
            .collect(partitioningBy(this::isPrime));
    }

    boolean isPrime(int candidate) {
        return IntStream.range(2, candidate)
            .noneMatch(i -> candidate % i == 0);
    }
}
