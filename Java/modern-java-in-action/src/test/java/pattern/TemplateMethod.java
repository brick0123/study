package pattern;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import pattern.Database.Customer;

public class TemplateMethod {

    @Test
    void use() {
        new OnlineBankingLambda()
            .processCustomer(1, c -> System.out.println("Happy"));
    }
}

abstract class OnlineBanking {

    public void processCustomer(int id) {
        final Customer customer = Database.findCustomerById(id);
        makeHappy(customer);
    }

    protected abstract void makeHappy(Customer customer);
}

class OnlineBankingLambda {

    public void processCustomer(int id, Consumer<Customer> makeHappy) {
        final Customer customer = Database.findCustomerById(id);
        makeHappy.accept(customer);
    }

}

class Database {

    public static Customer findCustomerById(int id) {
        return new Customer();
    }

    public static class Customer {

    }
}
