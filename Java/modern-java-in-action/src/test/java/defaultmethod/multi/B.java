package defaultmethod.multi;

public interface B extends A {

    default void hello() {
        System.out.println("B");
    }

}
