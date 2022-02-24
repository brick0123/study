package parallel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

public class SpliteratorTest {

    final String word = "H an ";

    @Test
    void word_count_iteratively() {
        final int result = countWordsIteratively(word);
        System.out.println("result = " + result);
    }

    public int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) {
                    counter++;
                }
                lastSpace = false;
            }
        }
        return counter;
    }

    @Test
    void word_count_functional() {
        final Stream<Character> stream = IntStream.range(0, word.length())
            .mapToObj(word::charAt);

        System.out.println("Found " + WordCounter.countWords(stream) + " Words");
    }

    @Test
    void word_count_functional_parallel() {
        // 임의의 위치에서 둘로 나누다보니, 하나의 단어를 둘로 계산하는 상황이 발생할 수 있다.
        // 즉 순차 스트림을 변렬 스트림으로 바꿀 때 스트림 분할 위치에 따라 잘못된 결과가 나올 수 있음
        final Stream<Character> stream = IntStream.range(0, word.length())
            .mapToObj(word::charAt);

        final int countWords = WordCounter.countWords(stream.parallel());
        assertEquals(3, countWords);
        System.out.println("Found " + countWords + " Words");
    }

    @Test
    void word_count_functional_parallel_2() {
        final Spliterator<Character> spliterator = new WordCounterSpliterator(word);
        final Stream<Character> stream = StreamSupport.stream(spliterator, true); // 병렬 스트림 생성 여부 ㅣㅈ시

        final int countWords = WordCounter.countWords(stream.parallel());
        assertEquals(2, countWords);
        System.out.println("Found " + countWords + " Words");
    }

    static class WordCounter {
        private final int counter;

        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;

        }

        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter + 1, false) : this;
            }
        }

        public WordCounter combine(WordCounter wordCounter) {
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
        public static int countWords(Stream<Character> stream) {
            final WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine);

            return wordCounter.getCounter();
        }
    }

    static class WordCounterSpliterator implements Spliterator<Character> {

        private final String string;
        private int currentChar = 0;

        public WordCounterSpliterator(String string) {
            this.string = string;
        }

        /**
         * 메서드 문자열에 현재 인덱스에 해당하는 문자를 Consumer에 제공한 다음에 인덱스를 증가시킨다.
         * 인수로 전달된 Consumer는 스트림을 탐색하면서 적용해야 하는 함수 집합이 작업을 처라할 수 있도록
         * 소비한 문자를 전달하는 내부 클래스.
         * 예제에서 스트림을 탐색하면서 하나의 리듀싱 함수, 즉 WordCounter의 accumulate 메서드만 적용한다.
         * tryAdvice 메서드는 새로운 커서 위치가 전체 문자열 길이보다 작으면 참을 반환하며, 반복 탐색해야할 문자가 남아 있음을 의미한다.
         *
         */
        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            action.accept(string.charAt(currentChar++)); // 현재 문자를 소비
            return currentChar < string.length(); // 소비할 문자가 남아있으면 true
        }

        /**
         * 반복될 자료구조를 분할하는 로직을 포함하므로 Spliterator에서 가장 중요한 메서드다.
         * RecursiveTask의 compute 메서드가 했던 것 처럼 우선 분할 동작을 중단할 한계를 설정해야한다.
         * 여기서는 아주 작은 한계값(10개의 문자)를 사용했지만, 실전의 애플리케이션은 너무 많은 태스크를 만들지 않도록 더 높은 한계값을 설정해야됨.
         * 분할 과정에서 남은 문자 수가 한계값 이하면 null 반환 (중단). 반대로 분할이 필요한 상황에는
         * 파싱해야 할 문자열 청크의 중간 위치를 기준으로 분할하도록 이동시킨다. 분할할 위치를 찾았으면 새로운 Spliterator를 만든다.
         * 새로만든 Spliterator는 현재 위치(currentChar) 부터 분할된 위치까지의 문자를 탐색한다.
         *
         */
        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                return null; // 파싱할 문자열을 순차적으로 처리할 수 있을 만큼 충분히 작아졌음을 알리는 Null을 반환
            }

            // 파싱할 문자열의 중간을 분할 위치로 설정한다.
            for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
                if (Character.isWhitespace(string.charAt(splitPos))) { // 다음 공백이 나올 때 까지 분할 위치를 뒤로 이동시킨다.
                    final WordCounterSpliterator spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos)); // 첨부터 분할 위치까지 문자열을 파싱할 ~를 생성한다.
                    currentChar = splitPos; // 이 Word~의 시작 위치를 분할 위치로 설정한다.
                    return spliterator; // 공백을 찾았고 문자열을 분리했으므로  루프 종
                }
            }
            return null;
        }

        /**
         * 탐색해야 할 요소의 개수(estimateSize)는 Spliterator가 파싱할 문자열 전체 길이 (string.length())와 현재 반복 중인 위치(currentSize)다.
         */
        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }

        /**
         * 프레임워크에 Spliterator가 ORDERED (문자열의 문자 등장 순서가 유의미함)
         * SIZED(estimatedSize 반환값이 정확함)
         * SUBSIZED(trySplit으로 생성된 Spliterator도 정확한 크기를 가짐)
         * NONNULL(문자열에는 null 문자가 존재하지 않음)
         * IMMUTABLE (문자열 자체가 불변 클래스이므로 문자열을 파싱하면서 속성이 추가되지 않음)
         * 등의 특성임을 알려줌
         */
        @Override
        public int characteristics() {
            return ORDERED + SIZED + NONNULL + IMMUTABLE;
        }
    }
}
