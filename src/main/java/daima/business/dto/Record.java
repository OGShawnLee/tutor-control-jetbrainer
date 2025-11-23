package daima.business.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Record interface representing an entity with a creation timestamp.
 * It provides a method to retrieve the creation date and a default method to format it.
 */
public interface Record {
  /**
   * Retrieves the creation date of the record.
   *
   * @return Creation date as LocalDateTime.
   */
  LocalDateTime getCreatedAt();

  /**
   * Formats the creation date into a human-readable string.
   *
   * @return Formatted creation date string.
   */
  default String getFormattedCreatedAt() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'a las' HH:mm", Locale.forLanguageTag("es-ES"));
    return getCreatedAt().format(formatter);
  }
}