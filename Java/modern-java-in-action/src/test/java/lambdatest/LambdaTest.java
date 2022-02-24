package lambdatest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LambdaTest {

    @Test
    void testMoveAllPoints() {
        final List<Point> points = List.of(new Point(5, 5), new Point(10, 5));
        final List<Point> expected = List.of(new Point(15, 5), new Point(20, 5));

        final List<Point> result = Point.moveAllPointsRightBy(points, 10);
        assertEquals(expected, result);
    }

    static class Point {

        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Point moveRightBy(int x) {
            return new Point(this.x + x, this.y);
        }

        public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
            return points.stream()
                .map(p -> new Point(p.getX() + x, p.getY()))
                .collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Point)) {
                return false;
            }
            final Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}
