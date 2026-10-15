package calendar.model.exception;

/**
 * Исключение, выбрасываемое при некорректных операциях с производственным календарём.
 */
public class CalendarException extends RuntimeException {

    /**
     * @param message описание ошибки
     */
    public CalendarException(String message) {
        super(message);
    }

    /**
     * @param message описание ошибки
     * @param cause   причина
     */
    public CalendarException(String message, Throwable cause) {
        super(message, cause);
    }
}