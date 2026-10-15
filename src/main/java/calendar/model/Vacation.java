package calendar.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Школьные каникулы — непрерывный интервал дат.
 *
 * @param start начало каникул (включительно)
 * @param end   конец каникул (включительно)
 * @param name  название
 */
public record Vacation(LocalDate start, LocalDate end, String name) implements CalendarRule {

    /**
     * Компактный конструктор с проверками.
     *
     * @throws NullPointerException     если любой из параметров равен {@code null}
     * @throws IllegalArgumentException если {@code end} раньше {@code start}
     */
    public Vacation {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end must not be before start");
        }
    }

    /**
     * Проверяет, попадает ли дата в интервал каникул.
     *
     * @param date проверяемая дата
     * @return {@code true}, если дата входит в интервал
     */
    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean matches(LocalDate date) {
        return contains(date);
    }

    @Override
    public DayType dayType() {
        return DayType.VACATION;
    }

    @Override
    public String describe() {
        return "Каникулы: " + name;
    }
}