package defaultmethod.collision;

public interface B {

    default void hello() {
        System.out.println("B");
    }

}
