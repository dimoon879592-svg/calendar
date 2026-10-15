package calendar.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Перенос выходного дня.
 *
 * @param from дата, с которой «сгорает» выходной
 * @param to   дата, на которую переносится выходной
 */
public record HolidayTransfer(LocalDate from, LocalDate to) implements CalendarRule {

    /**
     * Компактный конструктор с проверками.
     *
     * @throws NullPointerException     если {@code from} или {@code to} равны {@code null}
     * @throws IllegalArgumentException если {@code from} совпадает с {@code to}
     */
    public HolidayTransfer {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        if (from.equals(to)) {
            throw new IllegalArgumentException("from and to must differ");
        }
    }

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public boolean matches(LocalDate date) {
        return to.equals(date);
    }

    @Override
    public DayType dayType() {
        return DayType.WEEKEND;
    }

    @Override
    public String describe() {
        return "Перенос выходного с " + from + " на " + to;
    }
}