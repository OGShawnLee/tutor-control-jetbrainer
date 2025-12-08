package daima.business.dto;

import daima.business.enumeration.Semester;

public class PeriodDTO {
  private final int year;
  private final Semester semester;

  /**
   * Constructor for creating an PeriodDTO object with all fields from the database.
   *
   * @param year     The year of the period (e.g., 2024)
   * @param semester The semester of the period (FEB_JUL or AUG_JAN)
   */
  public PeriodDTO(int year, Semester semester) {
    this.year = year;
    this.semester = semester;
  }

  public int getYear() {
    return year;
  }

  public Semester getSemester() {
    return semester;
  }

  @Override
  public String toString() {
    return semester.toString() + " " + year;
  }
}
