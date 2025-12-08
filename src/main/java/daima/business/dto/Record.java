package daima.business.dto;

import daima.business.validator.Utility;

import java.time.LocalDateTime;

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
    return Utility.getFormattedLocalDateTime(getCreatedAt());
  }
}