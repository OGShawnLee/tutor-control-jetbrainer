package daima.business.dto;

import daima.business.enumeration.AppointmentState;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.validator.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TutoringSessionDTO implements Record, Searchable {
  private int id;
  private int idTutor;
  private int idTutored;
  private int idPlan;
  private String nameTutor;
  private String nameTutored;
  private String enrollment;
  private String hour;
  private TutoringSessionKind kind;
  private AppointmentState state;
  private LocalDate appointmentDate;
  private PeriodDTO periodDTO;
  private LocalDateTime createdAt;

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public int getIDTutor() {
    return idTutor;
  }

  public void setIDTutor(int idTutor) {
    this.idTutor = idTutor;
  }

  public int getIDTutored() {
    return idTutored;
  }

  public void setIDTutored(int idTutored) {
    this.idTutored = idTutored;
  }

  public int getIDPlan() {
    return idPlan;
  }

  public void setIDPlan(int idPlan) {
    this.idPlan = idPlan;
  }

  public String getNameTutor() {
    return nameTutor;
  }

  public void setNameTutor(String nameTutor) {
    this.nameTutor = nameTutor;
  }

  public String getNameTutored() {
    return nameTutored;
  }

  public void setNameTutored(String nameTutored) {
    this.nameTutored = nameTutored;
  }

  public String getEnrollment() {
    return nameTutored;
  }

  public void setEnrollment(String enrollment) {
    this.enrollment = enrollment;
  }

  public String getHour() {
    return hour;
  }

  public void setHour(String hour) {
    this.hour = hour;
  }

  public TutoringSessionKind getKind() {
    return kind;
  }

  public void setKind(TutoringSessionKind kind) {
    this.kind = kind;
  }

  public AppointmentState getState() {
    return state;
  }

  public void setState(AppointmentState state) {
    this.state = state;
  }

  public LocalDate getAppointmentDate() {
    return appointmentDate;
  }

  public void setAppointmentDate(LocalDate appointmentDate) {
    this.appointmentDate = appointmentDate;
  }

  public PeriodDTO getPeriodDTO() {
    return periodDTO;
  }

  public void setPeriodDTO(PeriodDTO periodDTO) {
    this.periodDTO = periodDTO;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getFormattedTutored() {
    return String.format("%s (S%s)", nameTutored, enrollment);
  }

  public String getFormattedAppointmentDate() {
    return String.format("%s %s", Utility.getFormattedLocalDate(appointmentDate), hour);
  }

  @Override
  public String getSearchableText() {
    return String.format("%s %s %s %s %s %s %s",
      getNameTutor(),
      getNameTutored(),
      getPeriodDTO(),
      getKind(),
      getState(),
      getFormattedAppointmentDate(),
      getFormattedCreatedAt()
    ).toLowerCase();
  }
}
