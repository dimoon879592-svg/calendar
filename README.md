# Производственный календарь

Учебный проект по дисциплине «Java». Модель производственного календаря
одного года: праздники, переносы выходных, каникулы, типы дней и подсчёты
рабочих дней/часов.

## Модель предметной области

| Класс | Назначение |
|---|---|
| `DayType` (enum) | Типы дней с полем «длительность рабочего дня»: `WORKING(8)`, `WEEKEND(0)`, `HOLIDAY(0)`, `PRE_HOLIDAY(7)`, `VACATION(0)` |
| `Holiday` (record) | Праздник: дата, название, переносится ли |
| `HolidayTransfer` (record) | Перенос выходного: дата откуда, дата куда |
| `Vacation` (record) | Каникулы: интервал + название |
| `DayInfo` (record) | Дата + её тип |
| `CalendarYear` | Основной класс календаря года |
| `CalendarException` | Собственное unchecked-исключение |

## Базовый уровень (реализовано)

- Печать календаря по месяцам с обозначением типа каждого дня (Р/В/П/С/К).
- Определение типа произвольной даты (`getDayType`, `getDayInfo`).
- Число рабочих дней и рабочих часов за месяц, квартал и год.
- Список праздников года с днями недели (`holidayReport`).

## Повышенный уровень (реализовано)

- Перенос выходного, совпавшего с праздником, на ближайший свободный рабочий день.
- Расчёт даты «через N рабочих дней» (`plusWorkingDays`).
- Число рабочих дней между двумя датами (`workingDaysBetween`).
- Школьные каникулы как отдельный тип дня (`VACATION`).

## Стек

- Java 21 (records, switch-выражения, `var`)
- Maven
- JUnit 5

## Запуск

```bash
mvn clean test
mvn compile exec:java
```

## Структура

```
src/main/java/calendar
├── Main.java
├── model
│   ├── CalendarYear.java
│   ├── DayInfo.java
│   ├── DayType.java
│   ├── Holiday.java
│   ├── HolidayTransfer.java
│   ├── Vacation.java
│   └── exception/CalendarException.java
└── demo/Demo.java
```