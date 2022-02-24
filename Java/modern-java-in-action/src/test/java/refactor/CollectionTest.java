package refactor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CollectionTest {

    @Test
    void list_factory() {
        List<String> friends = List.of("Hello", "World");
        assertThrows(UnsupportedOperationException.class,
            () -> friends.add("kim")
        );
        // list.of에서 가변인수를 사용하지 않고 오버로딩을 사용하는 이유

        /**
         * 내부적으로 가변 인수 버전은 추가 배열을 할당해서 감싼다. 따라서 배열을 할당하고 초기화하며 나중에 gc 비용을 지불해야한다.
         * 오버로딩을 이용해서 비용을 제거할 수 있다. 인수가 10개 이상 사용되면, 가변 인수를 이용하는 메서드가 생성된다.
         */
    }

    @Test
    void set_factory() {
        final Set<String> friends = Set.of("Hello", "World");
        System.out.println(friends);
    }

    @Test
    void map_factory() {
        final Map<String, Integer> ageOfFriends = Map.of("Hello", 10, "World", 20);
        System.out.println(ageOfFriends);
    }

    @Test
    void map_factory_2() {
        final Map<String, Integer> ageOfFriends = Map.ofEntries(
            Map.entry("Hello", 10),
            Map.entry("World", 20));

        System.out.println(ageOfFriends);
    }

    @Test
    void map_for_each() {
        final Map<String, Integer> ageOfFriends = Map.ofEntries(
            Map.entry("Hello", 10),
            Map.entry("World", 20));

        ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
    }

    @Test
    @DisplayName("comparingByKey, comparingByValue")
    void map_sort() {
        final Map<String, Integer> ageOfFriends = Map.ofEntries(
            Map.entry("Hello", 10),
            Map.entry("Abc", 10),
            Map.entry("Bcd", 10),
            Map.entry("World", 20));

        ageOfFriends.entrySet().stream()
            .sorted(Entry.comparingByKey())
            .forEach(System.out::println);

//        ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
    }

    // 교체 패턴

    @Test
    void replace_all() {
        final Map<String, String> movies = new HashMap<>();
        movies.put("Raphael", "Star Wars");
        movies.put("Olivia", "james bond");
        movies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println("movies = " + movies);
    }

    // 합침

    @Test
    void putAll() {
        final Map<String, String> family = Map.ofEntries(
            Map.entry("Teo", "Star Wars"),
            Map.entry("Cristina", "James Bond")
        );

        final Map<String, String> friends = Map.ofEntries(
            Map.entry("Raphael", "Star Wars")
        );

        final Map<String, String> everyone = new HashMap<>(family);
        everyone.putAll(friends);

        assertEquals(3, everyone.size());
        System.out.println("everyone = " + everyone);
    }


    @Test
    void merge() {
        final Map<String, String> family = Map.ofEntries(
            Map.entry("Teo", "Star Wars"),
            Map.entry("Cristina", "James Bond")
        );

        final Map<String, String> friends = Map.ofEntries(
            Map.entry("Raphael", "Star Wars"),
            Map.entry("Cristina", "Matrix")
        );

        final Map<String, String> everyone = new HashMap<>(family);
        friends.forEach((k, v) -> everyone.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2));

        assertEquals(3, everyone.size());
        System.out.println("everyone = " + everyone);
    }

    // ------------------ Concurrent Hash Map

    // reduce & search

    @Test
    void reduce() {
        final ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
        long parallelismThreshold = 1; // 1: 공통 스레드 풀을 이용해서 병렬성을 극대화함, Long.MAX_VALUE: 한 개의 스레드로 연산 실행
        final Optional<Long> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));

        System.out.println("maxValue = " + maxValue);
    }

    @Test
    @DisplayName("size대신 mappingCount를 사용하는 것이 좋다, 그래야 int의 범위를 넘어간 상황에 대처 가능")
    void mappingCount() {
        final ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();

        final long size = map.mappingCount();
        System.out.println("size = " + size);
    }
}
