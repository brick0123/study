package defaultmethod.multi;

public interface A {

    default void hello() {
        System.out.println("A");
    }

}
