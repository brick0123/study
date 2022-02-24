package pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * 인스턴스 로직을 클라이언트에 노출하지 않고 객체를 만들 때 사용
 */
public class Factory {

    @Test
    void use() {
        final Product product = ProductFactory.createProduct("loan");
    }
}

class ProductFactory {

    public static Product createProduct(String name) {
        switch (name) {
            case "loan":
                return new Loan();
            case "stock":
                return new Stock();
            default:
                throw new IllegalArgumentException();
        }
    }
}

class ProductFactoryLambda {

    private final static Map<String, Supplier<Product>> map = new HashMap<>();

    static {
        map.put("loan", Loan::new);
        map.put("stock", Stock::new);
    }

    public static Product createProduct(String name) {
        final Supplier<Product> productSupplier = map.get(name);
        if (Objects.nonNull(productSupplier)) {
            return productSupplier.get();
        }
        throw new IllegalStateException();
    }
}

abstract class Product {

}

class Loan extends Product {

}

class Stock extends Product {

}
