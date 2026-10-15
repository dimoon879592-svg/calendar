package calendar.model;

public enum DayType {
    WORKING(8.0, "Рабочий", "Р"),
    WEEKEND(0.0, "Выходной", "В"),
    HOLIDAY(0.0, "Праздник", "П"),
    PRE_HOLIDAY(7.0, "Предпраздничный сокращённый", "С"),
    VACATION(0.0, "Каникулы", "К");

    private final double workHours;
    private final String label;
    private final String symbol;

    DayType(double workHours, String label, String symbol) {
        this.workHours = workHours;
        this.label = label;
        this.symbol = symbol;
    }

    public double getWorkHours() { return workHours; }
    public String getLabel()     { return label; }
    public String getSymbol()    { return symbol; }
    public boolean isWorking()   { return workHours > 0; }
}