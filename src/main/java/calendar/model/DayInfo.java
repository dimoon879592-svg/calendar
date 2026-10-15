package calendar.model;

import java.time.LocalDate;

public record DayInfo(LocalDate date, DayType type) {
    public double workHours() {
        return type.getWorkHours();
    }
}