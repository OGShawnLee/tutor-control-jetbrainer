package daima.business.dto;

import daima.business.enumeration.Semester;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class TutoringSessionPlanDTO implements Record {
  private int id;
  private final LocalDate appointmentDate;
  private final TutoringSessionKind kind;
  private LocalDateTime createdAt = LocalDateTime.now();
  private final int idCoordinator;
  private String nameCoordinator;
  private final int idProgram;
  private final PeriodDTO period = new PeriodDTO(2024, Semester.FEB_JUL);

  public TutoringSessionPlanDTO(LocalDate appointmentDate, TutoringSessionKind kind, int idCoordinator, int idProgram) {
    this.appointmentDate = appointmentDate;
    this.kind = kind;
    this.idCoordinator = idCoordinator;
    this.nameCoordinator = "Shawn Lee";
    this.idProgram = idProgram;
  }

  public int getID() {
    return id;
  }

  public TutoringSessionPlanState getState() {
    return appointmentDate.isAfter(LocalDate.now())
      ? TutoringSessionPlanState.SCHEDULED
      : TutoringSessionPlanState.CONCLUDED;
  }

  public LocalDate getAppointmentDate() {
    return appointmentDate;
  }

  public TutoringSessionKind getKind() {
    return kind;
  }

  public int getIDCoordinator() {
    return idCoordinator;
  }

  public String getNameCoordinator() {
    return nameCoordinator;
  }

  public int getIDProgram() {
    return idProgram;
  }

  public PeriodDTO getPeriod() {
    return period;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
