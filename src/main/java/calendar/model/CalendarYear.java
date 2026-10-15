package calendar.model;

import calendar.model.exception.CalendarException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Производственный календарь одного года.
 * <p>Хранит праздники, переносы выходных и каникулы, умеет определять тип
 * любого дня, считать количество рабочих дней/часов за месяц, квартал и год,
 * а также печатать календарь по месяцам.</p>
 */
public final class CalendarYear {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2100;

    private final int year;
    private final List<Holiday> holidays = new ArrayList<>();
    private final List<Vacation> vacations = new ArrayList<>();
    private List<HolidayTransfer> transfers = new ArrayList<>();
    private boolean transfersDirty = true;

    /**
     * @param year год календаря (1900..2100)
     * @throws CalendarException если год вне допустимого диапазона
     */
    public CalendarYear(int year) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new CalendarException(
                    "Год должен быть в диапазоне " + MIN_YEAR + ".." + MAX_YEAR + ", получено: " + year);
        }
        this.year = year;
    }

    /** @return год календаря */
    public int getYear() {
        return year;
    }

    /**
     * Добавляет праздник.
     *
     * @param holiday праздник
     * @throws NullPointerException если {@code holiday == null}
     * @throws CalendarException    если праздник не относится к этому году
     *                              или праздник на такую дату уже добавлен
     */
    public void addHoliday(Holiday holiday) {
        Objects.requireNonNull(holiday, "holiday must not be null");
        if (holiday.date().getYear() != year) {
            throw new CalendarException(
                    "Праздник \"" + holiday.name() + "\" от " + holiday.date()
                            + " не относится к году " + year);
        }
        for (Holiday existing : holidays) {
            if (existing.date().equals(holiday.date())) {
                throw new CalendarException(
                        "Праздник на дату " + holiday.date() + " уже добавлен: " + existing.name());
            }
        }
        holidays.add(holiday);
        transfersDirty = true;
    }

    /**
     * Добавляет период каникул.
     *
     * @param vacation каникулы
     * @throws NullPointerException если {@code vacation == null}
     * @throws CalendarException    если каникулы не пересекаются с годом календаря
     */
    public void addVacation(Vacation vacation) {
        Objects.requireNonNull(vacation, "vacation must not be null");
        boolean overlaps = vacation.start().getYear() == year
                || vacation.end().getYear() == year
                || (vacation.start().getYear() < year && vacation.end().getYear() > year);
        if (!overlaps) {
            throw new CalendarException("Каникулы \"" + vacation.name()
                    + "\" не пересекаются с годом " + year);
        }
        vacations.add(vacation);
        transfersDirty = true;
    }

    /**
     * @return неизменяемый список праздников года
     */
    public List<Holiday> getHolidays() {
        return List.copyOf(holidays);
    }

    /**
     * @return неизменяемый список переносов выходных
     */
    public List<HolidayTransfer> getTransfers() {
        ensureTransfers();
        return List.copyOf(transfers);
    }

    /**
     * @return неизменяемый список каникул
     */
    public List<Vacation> getVacations() {
        return List.copyOf(vacations);
    }

    /**
     * Возвращает все правила календаря (праздники, каникулы, переносы)
     * в порядке приоритета. Используется для полиморфной обработки через
     * интерфейс {@link CalendarRule}.
     *
     * @return неизменяемый список правил
     */
    public List<CalendarRule> getRules() {
        ensureTransfers();
        List<CalendarRule> rules = new ArrayList<>();
        rules.addAll(vacations);
        rules.addAll(holidays);
        rules.addAll(transfers);
        rules.sort(Comparator.comparingInt(CalendarRule::priority));
        return List.copyOf(rules);
    }

    /**
     * Определяет тип произвольной даты этого года.
     * Использует полиморфный обход правил {@link CalendarRule}.
     *
     * @param date дата
     * @return тип дня
     * @throws NullPointerException если {@code date == null}
     * @throws CalendarException    если дата не относится к году календаря
     */
    public DayType getDayType(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        if (date.getYear() != year) {
            throw new CalendarException("Дата " + date + " не относится к году " + year);
        }
        ensureTransfers();

        // Полиморфный проход: единый интерфейс CalendarRule для
        // Holiday, Vacation и HolidayTransfer.
        List<CalendarRule> rules = new ArrayList<>();
        rules.addAll(vacations);
        rules.addAll(holidays);
        rules.addAll(transfers);
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
                if (h.date().equals(next)) {
                    return DayType.PRE_HOLIDAY;
                }
            }
        }
        return DayType.WORKING;
    }

    /**
     * Возвращает информацию о произвольной дате.
     *
     * @param date дата
     * @return объект {@link DayInfo}
     */
    public DayInfo getDayInfo(LocalDate date) {
        return new DayInfo(date, getDayType(date));
    }

    /**
     * @param ym месяц
     * @return число рабочих дней в месяце
     * @throws CalendarException если месяц не относится к году календаря
     */
    public int workingDaysInMonth(YearMonth ym) {
        requireMonth(ym);
        int count = 0;
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            if (getDayType(ym.atDay(d)).isWorking()) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param ym месяц
     * @return суммарное число рабочих часов в месяце
     * @throws CalendarException если месяц не относится к году календаря
     */
    public double workingHoursInMonth(YearMonth ym) {
        requireMonth(ym);
        double sum = 0.0;
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            sum += getDayType(ym.atDay(d)).getWorkHours();
        }
        return sum;
    }

    /**
     * @param quarter номер квартала (1..4)
     * @return число рабочих дней в квартале
     * @throws CalendarException если номер квартала вне диапазона
     */
    public int workingDaysInQuarter(int quarter) {
        requireQuarter(quarter);
        int count = 0;
        for (int m = 1; m <= 3; m++) {
            count += workingDaysInMonth(YearMonth.of(year, (quarter - 1) * 3 + m));
        }
        return count;
    }

    /**
     * @param quarter номер квартала (1..4)
     * @return суммарное число рабочих часов в квартале
     * @throws CalendarException если номер квартала вне диапазона
     */
    public double workingHoursInQuarter(int quarter) {
        requireQuarter(quarter);
        double sum = 0.0;
        for (int m = 1; m <= 3; m++) {
            sum += workingHoursInMonth(YearMonth.of(year, (quarter - 1) * 3 + m));
        }
        return sum;
    }

    /** @return число рабочих дней в году */
    public int workingDaysInYear() {
        int count = 0;
        for (int m = 1; m <= 12; m++) {
            count += workingDaysInMonth(YearMonth.of(year, m));
        }
        return count;
    }

    /** @return суммарное число рабочих часов в году */
    public double workingHoursInYear() {
        double sum = 0.0;
        for (int m = 1; m <= 12; m++) {
            sum += workingHoursInMonth(YearMonth.of(year, m));
        }
        return sum;
    }

    /**
     * Печатает календарь месяца с обозначением типа каждого дня.
     *
     * @param ym месяц
     * @return строковое представление месяца
     * @throws CalendarException если месяц не относится к году календаря
     */
    public String printMonth(YearMonth ym) {
        requireMonth(ym);
        StringBuilder sb = new StringBuilder();
        sb.append(monthTitle(ym.getMonth())).append(' ').append(year).append('\n');
        sb.append("Пн   Вт   Ср   Чт   Пт   Сб   Вс\n");

        LocalDate first = ym.atDay(1);
        int startCol = first.getDayOfWeek().getValue() - 1;
        sb.append("     ".repeat(startCol));
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            DayType type = getDayType(ym.atDay(d));
            sb.append(String.format("%2d%-2s ", d, type.getSymbol()));
            int col = (startCol + d - 1) % 7;
            if (col == 6) {
                sb.append('\n');
            }
        }
        if (sb.charAt(sb.length() - 1) != '\n') {
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Возвращает список праздников года с днями недели.
     *
     * @return список строк вида "01.01.2025 (Среда) — Новый год"
     */
    public List<String> holidayReport() {
        List<Holiday> sorted = new ArrayList<>(holidays);
        sorted.sort(Comparator.comparing(Holiday::date));
        List<String> out = new ArrayList<>(sorted.size());
        for (Holiday h : sorted) {
            out.add(String.format("%s (%s) — %s",
                    h.date(), weekdayName(h.date().getDayOfWeek()), h.name()));
        }
        return out;
    }

    /**
     * Находит дату, отстоящую от {@code start} на {@code n} рабочих дней вперёд.
     *
     * @param start начальная дата
     * @param n     количество рабочих дней (>= 0)
     * @return дата через N рабочих дней
     * @throws NullPointerException если {@code start == null}
     * @throws CalendarException    если {@code n < 0} или выходит за пределы года
     */
    public LocalDate plusWorkingDays(LocalDate start, int n) {
        Objects.requireNonNull(start, "start must not be null");
        if (n < 0) {
            throw new CalendarException("Количество рабочих дней не может быть отрицательным: " + n);
        }
        LocalDate cur = start;
        int added = 0;
        while (added < n) {
            cur = cur.plusDays(1);
            if (cur.getYear() != year) {
                throw new CalendarException("Дата вышла за пределы года " + year);
            }
            if (getDayType(cur).isWorking()) {
                added++;
            }
        }
        return cur;
    }

    /**
     * Считает количество рабочих дней между двумя датами включительно.
     *
     * @param from начальная дата
     * @param to   конечная дата
     * @return число рабочих дней
     * @throws NullPointerException если любой аргумент равен {@code null}
     * @throws CalendarException    если {@code to} раньше {@code from} или даты вне года
     */
    public int workingDaysBetween(LocalDate from, LocalDate to) {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        if (to.isBefore(from)) {
            throw new CalendarException("Конечная дата раньше начальной: " + to + " < " + from);
        }
        int count = 0;
        LocalDate cur = from;
        while (!cur.isAfter(to)) {
            if (cur.getYear() != year) {
                throw new CalendarException("Дата " + cur + " не относится к году " + year);
            }
            if (getDayType(cur).isWorking()) {
                count++;
            }
            cur = cur.plusDays(1);
        }
        return count;
    }

    // ---------- internals ----------

    private void requireMonth(YearMonth ym) {
        if (ym.getYear() != year) {
            throw new CalendarException("Месяц " + ym + " не относится к году " + year);
        }
    }

    private void requireQuarter(int quarter) {
        if (quarter < 1 || quarter > 4) {
            throw new CalendarException("Номер квартала должен быть 1..4, получено: " + quarter);
        }
    }

    private void ensureTransfers() {
        if (!transfersDirty) {
            return;
        }
        List<Holiday> sortedHolidays = new ArrayList<>(holidays);
        sortedHolidays.sort(Comparator.comparing(Holiday::date));

        List<HolidayTransfer> result = new ArrayList<>();
        Set<LocalDate> usedTargets = new HashSet<>();

        for (Holiday h : sortedHolidays) {
            if (!h.transferable()) {
                continue;
            }
            DayOfWeek dow = h.date().getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
                continue;
            }
            LocalDate candidate = h.date().plusDays(1);
            while (isBusy(candidate, sortedHolidays, usedTargets)) {
                candidate = candidate.plusDays(1);
                if (candidate.getYear() > year) {
                    throw new CalendarException(
                            "Не удалось найти день для переноса праздника \"" + h.name() + "\"");
                }
            }
            result.add(new HolidayTransfer(h.date(), candidate));
            usedTargets.add(candidate);
        }

        result.sort(Comparator.comparing(HolidayTransfer::to));
        this.transfers = result;
        this.transfersDirty = false;
    }

    private boolean isBusy(LocalDate date, List<Holiday> sortedHolidays, Set<LocalDate> usedTargets) {
        if (usedTargets.contains(date)) {
            return true;
        }
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return true;
        }
        for (Holiday h : sortedHolidays) {
            if (h.date().equals(date)) {
                return true;
            }
        }
        for (Vacation v : vacations) {
            if (v.contains(date)) {
                return true;
            }
        }
        return false;
    }

    private static String monthTitle(Month month) {
        return switch (month) {
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

    private static String weekdayName(DayOfWeek dow) {
        return switch (dow) {
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