package calendar.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Информация о конкретном дне календаря.
 *
 * @param date дата
 * @param type тип дня
 */
public record DayInfo(LocalDate date, DayType type) {

    /**
     * Компактный конструктор с проверками.
     *
     * @throws NullPointerException если любой из параметров равен {@code null}
     */
    public DayInfo {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }

    /** @return длительность рабочего дня в часах */
    public double workHours() {
        return type.getWorkHours();
    }
}