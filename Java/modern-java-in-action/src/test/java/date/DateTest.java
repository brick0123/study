package date;

import static java.time.temporal.TemporalAdjusters.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.Optional;
import java.util.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DateTest {

    @Test
    void duration() {
        final LocalTime time1 = LocalTime.of(22, 0, 0);
        final LocalTime time2 = LocalTime.of(7, 0, 0);

        final Duration between = Duration.between(time1, time2);
        System.out.println("between = " + between);
    }

    @Test
    void period() {
        final LocalDate date1 = LocalDate.of(2021, 1, 1);
        final LocalDate date2 = LocalDate.of(2021, 1, 10);

        final Period between = Period.between(date1, date2);
        System.out.println("between = " + between);
    }

    @Test
    void temporalAdjusters() {
        final LocalDate date1 = LocalDate.of(2021, 12, 14);
        final LocalDate date2 = date1.with(nextOrSame(DayOfWeek.MONDAY));
        final LocalDate date3 = date2.with(lastDayOfMonth());

        System.out.println("date1 = " + date1);
        System.out.println("date2 = " + date2);
        System.out.println("date3 = " + date3);
    }

    @Test
    void next_day() {
        final LocalDate date = LocalDate.of(2021, 12, 17);
        final LocalDate after = date.with(new NextNetworkingDay());

        assertEquals(20, after.getDayOfMonth());
    }

    @Test
    void format() {
        final LocalDate date = LocalDate.of(2021, 12, 14);
        final String format = date.format(DateTimeFormatter.ISO_DATE);

        System.out.println("format = " + format);
    }

    @Test
    void format2() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final LocalDate date = LocalDate.of(2021, 12, 14);

        final String formattedDate = date.format(formatter);
        System.out.println("formattedDate = " + formattedDate);

        final LocalDate date2 = LocalDate.parse(formattedDate, formatter);
        System.out.println("date2 = " + date2);
    }
    // |LocalDate|LocalTime|ZoneId
    // |    LocalDateTime  |
    // |        ZoneDateTime       |

    @Test
    void zone() {
        final ZoneId zoneId = TimeZone.getDefault().toZoneId();

        final LocalDate date = LocalDate.of(2021, 12, 14);
        final ZonedDateTime zonedDateTime = date.atStartOfDay(zoneId);

        System.out.println("zonedDateTime = " + zonedDateTime);
    }

    static class NextNetworkingDay implements TemporalAdjuster {

        @Override
        public Temporal adjustInto(Temporal temporal) {
            final DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));

            int plusDays = 1;
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                plusDays = 3;
            } else if (dayOfWeek == DayOfWeek.SATURDAY) {
                plusDays = 2;
            }
            return temporal.plus(plusDays, ChronoUnit.DAYS);
        }

    }
}
