package calendar.model;

import calendar.model.exception.CalendarException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CalendarYear {

    private final int year;
    private final List<Holiday> holidays = new ArrayList<>();
    private final List<Vacation> vacations = new ArrayList<>();

    public CalendarYear(int year) {
        if (year < 1900 || year > 2100) {
            throw new CalendarException("Год должен быть в диапазоне 1900..2100, получено: " + year);
        }
        this.year = year;
    }

    public int getYear() { return year; }

    public void addHoliday(Holiday h) {
        if (h.date().getYear() != year) {
            throw new CalendarException("Праздник \"" + h.name() + "\" от " + h.date()
                    + " не относится к году " + year);
        }
        holidays.add(h);
    }

    public void addVacation(Vacation v) {
        vacations.add(v);
    }

    public List<Holiday> getHolidays() { return List.copyOf(holidays); }
    public List<Vacation> getVacations() { return List.copyOf(vacations); }

    public DayType getDayType(LocalDate date) {
        if (date.getYear() != year) {
            throw new CalendarException("Дата " + date + " не относится к году " + year);
        }
        List<CalendarRule> rules = new ArrayList<>();
        rules.addAll(vacations);
        rules.addAll(holidays);
        rules.sort(Comparator.comparingInt(CalendarRule::priority));
        for (CalendarRule rule : rules) {
            if (rule.matches(date)) {
                return rule.dayType();
            }
        }
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return DayType.WEEKEND;
        }
        LocalDate next = date.plusDays(1);
        if (next.getYear() == year) {
            for (Holiday h : holidays) {
                if (h.date().equals(next)) return DayType.PRE_HOLIDAY;
            }
        }
        return DayType.WORKING;
    }

    public DayInfo getDayInfo(LocalDate date) {
        return new DayInfo(date, getDayType(date));
    }

    public int workingDaysInMonth(YearMonth ym) {
        checkMonth(ym);
        int count = 0;
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            if (getDayType(ym.atDay(d)).isWorking()) count++;
        }
        return count;
    }

    public double workingHoursInMonth(YearMonth ym) {
        checkMonth(ym);
        double sum = 0;
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            sum += getDayType(ym.atDay(d)).getWorkHours();
        }
        return sum;
    }

    public int workingDaysInQuarter(int q) {
        if (q < 1 || q > 4) {
            throw new CalendarException("Номер квартала должен быть 1..4, получено: " + q);
        }
        int count = 0;
        for (int m = 1; m <= 3; m++) {
            count += workingDaysInMonth(YearMonth.of(year, (q - 1) * 3 + m));
        }
        return count;
    }

    public double workingHoursInQuarter(int q) {
        if (q < 1 || q > 4) {
            throw new CalendarException("Номер квартала должен быть 1..4, получено: " + q);
        }
        double sum = 0;
        for (int m = 1; m <= 3; m++) {
            sum += workingHoursInMonth(YearMonth.of(year, (q - 1) * 3 + m));
        }
        return sum;
    }

    public int workingDaysInYear() {
        int count = 0;
        for (int m = 1; m <= 12; m++) count += workingDaysInMonth(YearMonth.of(year, m));
        return count;
    }

    public double workingHoursInYear() {
        double sum = 0;
        for (int m = 1; m <= 12; m++) sum += workingHoursInMonth(YearMonth.of(year, m));
        return sum;
    }

    public String printMonth(YearMonth ym) {
        checkMonth(ym);
        StringBuilder sb = new StringBuilder();
        sb.append(monthName(ym.getMonth())).append(' ').append(year).append('\n');
        sb.append("Пн   Вт   Ср   Чт   Пт   Сб   Вс\n");

        int startCol = ym.atDay(1).getDayOfWeek().getValue() - 1;
        sb.append("     ".repeat(startCol));
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            DayType t = getDayType(ym.atDay(d));
            sb.append(String.format("%2d%-2s ", d, t.getSymbol()));
            if ((startCol + d - 1) % 7 == 6) sb.append('\n');
        }
        if (sb.charAt(sb.length() - 1) != '\n') sb.append('\n');
        return sb.toString();
    }

    public List<String> holidayReport() {
        List<Holiday> sorted = new ArrayList<>(holidays);
        sorted.sort(Comparator.comparing(Holiday::date));
        List<String> out = new ArrayList<>();
        for (Holiday h : sorted) {
            out.add(String.format("%s (%s) — %s",
                    h.date(), weekdayName(h.date().getDayOfWeek()), h.name()));
        }
        return out;
    }

    private void checkMonth(YearMonth ym) {
        if (ym.getYear() != year) {
            throw new CalendarException("Месяц " + ym + " не относится к году " + year);
        }
    }

    private static String monthName(Month m) {
        return switch (m) {
            case JANUARY -> "Январь";
            case FEBRUARY -> "Февраль";
            case MARCH -> "Март";
            case APRIL -> "Апрель";
            case MAY -> "Май";
            case JUNE -> "Июнь";
            case JULY -> "Июль";
            case AUGUST -> "Август";
            case SEPTEMBER -> "Сентябрь";
            case OCTOBER -> "Октябрь";
            case NOVEMBER -> "Ноябрь";
            case DECEMBER -> "Декабрь";
        };
    }

    private static String weekdayName(DayOfWeek d) {
        return switch (d) {
            case MONDAY -> "Понедельник";
            case TUESDAY -> "Вторник";
            case WEDNESDAY -> "Среда";
            case THURSDAY -> "Четверг";
            case FRIDAY -> "Пятница";
            case SATURDAY -> "Суббота";
            case SUNDAY -> "Воскресенье";
        };
    }
}