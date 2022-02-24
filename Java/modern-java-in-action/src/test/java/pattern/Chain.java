package pattern;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

/**
 * 의무 체인
 * 한 객체가 어떤 작업을 처리한 다음에 다른 객체로 결과를 전달하고
 * 다른 객체도 해야할 작업을 처리한 다음에 다른 객체에게 전달..
 */
public class Chain {

    @Test
    void use() {
        final ProcessingObject<String> p1 = new HeaderTextProcessing();
        final ProcessingObject<String> p2 = new SpellCheckProcessing();
        p1.setSuccessor(p2);
        final String result = p1.handle("action labda");
        System.out.println("result = " + result);
    }

    @Test
    void use_lambda() {
        UnaryOperator<String> headerProcessing = (text) -> "From " + text;
        UnaryOperator<String> spellCheckProcessing = (text) -> text.replaceAll("labda", "lambda");

        final Function<String, String> pipeline = headerProcessing.andThen(spellCheckProcessing);

        final String result = pipeline.apply("action labda");
        System.out.println("result = " + result);
    }
}

abstract class ProcessingObject<T> {

    protected ProcessingObject<T> successor;

    public void setSuccessor(ProcessingObject<T> successor) {
        this.successor = successor;
    }

    // 템플릿 메서드 패턴이 사용됨
    public T handle(T input) {
        T r = handleWork(input);
        if (Objects.nonNull(successor)) {
            return successor.handle(r);
        }
        return r;
    }

    abstract T handleWork(T input);
}

class HeaderTextProcessing extends ProcessingObject<String> {

    @Override
    String handleWork(String input) {
        return "From " + input;
    }
}

class SpellCheckProcessing extends ProcessingObject<String> {

    @Override
    String handleWork(String input) {
        return input.replaceAll("labda", "lambda");
    }
}
