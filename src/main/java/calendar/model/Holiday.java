package calendar.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Праздничный день года.
 *
 * @param date         дата праздника
 * @param name         название
 * @param transferable переносится ли выходной, если праздник попал на выходной
 */
public record Holiday(LocalDate date, String name, boolean transferable) implements CalendarRule {

    /**
     * Компактный конструктор с проверками.
     *
     * @throws NullPointerException     если {@code date} или {@code name} равны {@code null}
     * @throws IllegalArgumentException если название пустое
     */
    public Holiday {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Holiday name must not be blank");
        }
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean matches(LocalDate d) {
        return date.equals(d);
    }

    @Override
    public DayType dayType() {
        return DayType.HOLIDAY;
    }

    @Override
    public String describe() {
        return "Праздник: " + name;
    }
}