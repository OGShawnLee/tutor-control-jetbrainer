package daima.business.dto;

import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.business.validator.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TutoringSessionPlanDTO implements Record, Searchable {
  private int id;
  private int idProgram;
  private PeriodDTO periodDTO;
  private TutoringSessionPlanState state;
  private TutoringSessionKind kind;
  private LocalDate appointmentDate;
  private LocalDateTime createdAt;

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public int getIDProgram() {
    return idProgram;
  }

  public void setIDProgram(int idProgram) {
    this.idProgram = idProgram;
  }

  public PeriodDTO getPeriodDTO() {
    return periodDTO;
  }

  public void setPeriodDTO(PeriodDTO periodDTO) {
    this.periodDTO = periodDTO;
  }

  public TutoringSessionPlanState getState() {
    return state;
  }

  public void setState(TutoringSessionPlanState state) {
    this.state = state;
  }

  public TutoringSessionKind getKind() {
    return kind;
  }

  public void setKind(TutoringSessionKind kind) {
    this.kind = kind;
  }

  public LocalDate getAppointmentDate() {
    return appointmentDate;
  }

  public void setAppointmentDate(LocalDate appointmentDate) {
    this.appointmentDate = appointmentDate;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getFormattedAppointmentDate() {
    return Utility.getFormattedLocalDate(appointmentDate);
  }

  @Override
  public String getSearchableText() {
    return String.format("%s %s %s %s %s",
      getPeriodDTO(),
      getKind(),
      getState(),
      getFormattedAppointmentDate(),
      getFormattedCreatedAt()
    ).toLowerCase();
  }
}
