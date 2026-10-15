package calendar.model;

import java.time.LocalDate;

public record Vacation(LocalDate start, LocalDate end, String name) implements CalendarRule {

    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    @Override
    public int priority() { return 1; }

    @Override
    public boolean matches(LocalDate date) { return contains(date); }

    @Override
    public DayType dayType() { return DayType.VACATION; }

    @Override
    public String describe() { return "Каникулы: " + name; }
}