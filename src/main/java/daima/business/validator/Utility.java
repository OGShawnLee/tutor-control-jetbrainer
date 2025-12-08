package daima.business.validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utility {
  private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
    "dd '/' MMMM '/' yyyy, HH:mm",
    Locale.forLanguageTag("es-ES")
  );
  private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(
    "dd '/' MMMM '/' yyyy",
    Locale.forLanguageTag("es-ES")
  );

  public static String getFormattedLocalDate(LocalDate date) {
    return date.format(LOCAL_DATE_FORMATTER);
  }

  public static String getFormattedLocalDateTime(LocalDateTime dateTime) {
    return dateTime.format(LOCAL_DATE_TIME_FORMATTER);
  }
}
