package calendar.model;

import java.time.LocalDate;

public record Holiday(LocalDate date, String name, boolean transferable) implements CalendarRule {

    @Override
    public int priority() { return 2; }

    @Override
    public boolean matches(LocalDate d) { return date.equals(d); }

    @Override
    public DayType dayType() { return DayType.HOLIDAY; }

    @Override
    public String describe() { return "Праздник: " + name; }
}