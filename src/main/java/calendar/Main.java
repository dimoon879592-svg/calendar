package calendar;

import calendar.model.CalendarYear;
import calendar.model.DayInfo;
import calendar.model.Holiday;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Производственный календарь ===\n");

        CalendarYear cal = new CalendarYear(2025);

        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 1), "Новый год", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 2), "Новогодние каникулы", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 3), "Новогодние каникулы", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 4), "Новогодние каникулы", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 5), "Новогодние каникулы", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 6), "Новогодние каникулы", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 7), "Рождество Христово", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 1, 8), "Новогодние каникулы", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 2, 23), "День защитника Отечества", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 3, 8), "Международный женский день", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 5, 1), "Праздник Весны и Труда", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 5, 9), "День Победы", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 6, 12), "День России", true));
        cal.addHoliday(new Holiday(LocalDate.of(2025, 11, 4), "День народного единства", true));

        // 1. Печать календаря по месяцам
        System.out.println("--- 1. Печать календаря по месяцам ---");
        for (Month m : new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH}) {
            System.out.println(cal.printMonth(YearMonth.of(2025, m)));
        }

        // 2. Определение типа произвольной даты
        System.out.println("--- 2. Определение типа произвольной даты ---");
        LocalDate[] dates = {
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 2, 23),
                LocalDate.of(2025, 3, 7),
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 10, 15)
        };
        for (LocalDate d : dates) {
            DayInfo info = cal.getDayInfo(d);
            System.out.printf("%s: %s (%.1f ч)%n",
                    info.date(), info.type().getLabel(), info.workHours());
        }
        System.out.println();

        // 3. Рабочие дни и часы по месяцам
        System.out.println("--- 3. Рабочие дни и часы по месяцам ---");
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        for (int i = 0; i < 12; i++) {
            YearMonth ym = YearMonth.of(2025, i + 1);
            System.out.printf("%-10s: %2d раб. дней, %5.1f ч%n",
                    monthNames[i], cal.workingDaysInMonth(ym), cal.workingHoursInMonth(ym));
        }
        System.out.println();

        // 4. Рабочие дни и часы по кварталам и году
        System.out.println("--- 4. Рабочие дни и часы по кварталам ---");
        for (int q = 1; q <= 4; q++) {
            System.out.printf("Q%d: %d раб. дней, %.1f ч%n",
                    q, cal.workingDaysInQuarter(q), cal.workingHoursInQuarter(q));
        }
        System.out.printf("Год: %d раб. дней, %.1f ч%n%n",
                cal.workingDaysInYear(), cal.workingHoursInYear());

        // 5. Праздники с днями недели
        System.out.println("--- 5. Праздники 2025 с днями недели ---");
        for (String line : cal.holidayReport()) {
            System.out.println("  " + line);
        }
    }
}