package calendar.model;

import java.time.LocalDate;

/**
 * Правило календаря: определяет, попадает ли дата под это правило,
 * и какой тип дня оно задаёт. Реализуется праздниками, каникулами и переносами.
 * <p>Полиморфный контракт: один и тот же вызов {@link #matches(LocalDate)}
 * на разных реализациях даёт разный результат.</p>
 */
public interface CalendarRule {

    /**
     * @return приоритет правила (чем меньше — тем раньше проверяется)
     */
    int priority();

    /**
     * @param date проверяемая дата
     * @return {@code true}, если дата попадает под правило
     */
    boolean matches(LocalDate date);

    /**
     * @return тип дня, который задаёт это правило
     */
    DayType dayType();

    /**
     * @return словесное описание правила
     */
    String describe();
}