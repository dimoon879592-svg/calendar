package calendar.model;

/**
 * Тип дня в производственном календаре.
 * Каждая константа хранит длительность рабочего дня (в часах),
 * удобочитаемое название и символ для печати календаря.
 */
public enum DayType {
    /** Обычный рабочий день, 8 часов. */
    WORKING(8.0, "Рабочий", "Р"),
    /** Выходной (суббота/воскресенье или перенесённый день). */
    WEEKEND(0.0, "Выходной", "В"),
    /** Праздничный день. */
    HOLIDAY(0.0, "Праздник", "П"),
    /** Сокращённый предпраздничный день, 7 часов. */
    PRE_HOLIDAY(7.0, "Предпраздничный сокращённый", "С"),
    /** Школьные каникулы. */
    VACATION(0.0, "Каникулы", "К");

    private final double workHours;
    private final String label;
    private final String symbol;

    DayType(double workHours, String label, String symbol) {
        this.workHours = workHours;
        this.label = label;
        this.symbol = symbol;
    }

    /** @return длительность рабочего дня в часах */
    public double getWorkHours() {
        return workHours;
    }

    /** @return название для вывода пользователю */
    public String getLabel() {
        return label;
    }

    /** @return символ для печати календаря */
    public String getSymbol() {
        return symbol;
    }

    /** @return {@code true}, если в этот день работают */
    public boolean isWorking() {
        return workHours > 0;
    }
}