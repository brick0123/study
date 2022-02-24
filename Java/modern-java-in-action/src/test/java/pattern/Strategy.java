package pattern;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 런타임에 적절한 알고리즘 선택하는 기법
 * 다양한 기준을 갖는 입력값을 검증, 파싱 등..
 */
public class Strategy {

    @Test
    void use() {
        final Validator numericValidator = new Validator(new IsNumeric());
        final boolean result1 = numericValidator.validate("aaa");

        final Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
        final boolean result2 = lowerCaseValidator.validate("bbb");

        assertFalse(result1);
        assertTrue(result2);
    }

    @Test
    void use_lambda() {
        final Validator numericValidator = new Validator((s) -> s.matches("\\d+"));
        final boolean result1 = numericValidator.validate("aaa");

        final Validator lowerCaseValidator = new Validator((s) -> s.matches("[a-z]+"));
        final boolean result2 = lowerCaseValidator.validate("bbb");

        assertFalse(result1);
        assertTrue(result2);
    }
}

interface ValidateStrategy {

    boolean execute(String s);
}

class IsAllLowerCase implements ValidateStrategy {

    @Override
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}

class IsNumeric implements ValidateStrategy {

    @Override
    public boolean execute(String s) {
        return s.matches("\\d+");
    }
}

class Validator {

    private final ValidateStrategy strategy;

    public Validator(ValidateStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean validate(String s) {
        return strategy.execute(s);
    }
}
