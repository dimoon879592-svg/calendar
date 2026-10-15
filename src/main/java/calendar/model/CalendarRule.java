package calendar.model;

import java.time.LocalDate;

public interface CalendarRule {
    int priority();
    boolean matches(LocalDate date);
    DayType dayType();
    String describe();
}