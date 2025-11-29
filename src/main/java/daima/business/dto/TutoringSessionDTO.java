package daima.business.dto;

import daima.business.enumeration.AppointmentState;
import daima.business.enumeration.Semester;
import daima.business.enumeration.TutoringSessionKind;

import java.time.LocalDate;

public class TutoringSessionDTO {
  private int id;
  private LocalDate appointmentDate;
  private String hour;
  private AppointmentState state;
  private TutoringSessionKind kind;
  private int idTutored;
  private String nameTutored;
  private String enrollmentTutored;
  private int idTutor;
  private String nameTutor;
  private PeriodDTO period;

  public TutoringSessionDTO(AppointmentState state, TutoringSessionKind kind) {
    this.id = 1;
    this.appointmentDate = LocalDate.now().minusDays(1);
    this.hour = "15:00";
    this.state = state;
    this.kind = kind;
    this.idTutored = 1001;
    this.nameTutored = "Alice Johnson";
    this.idTutor = 2001;
    this.nameTutor = "Dr. Robert Brown";
    this.enrollmentTutored = "S2400001";
    this.period = new PeriodDTO(2024, Semester.FEB_JUL);
  }

  public int getID() {
    return id;
  }

  public LocalDate getAppointmentDate() {
    return appointmentDate;
  }

  public String getHour() {
    return hour;
  }

  public AppointmentState getState() {
    if (state == AppointmentState.COMPLETED) return state;

    return appointmentDate.isBefore(LocalDate.now()) ? AppointmentState.MISSED : state;
  }

  public TutoringSessionKind getKind() {
    return kind;
  }

  public int getIDTutored() {
    return idTutored;
  }

  public String getNameTutored() {
    return nameTutored;
  }

  public String getEnrollmentTutored() {
    return enrollmentTutored;
  }

  public String getFormattedTutored() {
    return nameTutored + " (" + enrollmentTutored + ")";
  }

  public String getFormattedAppointmentDate() {
    return appointmentDate.toString() + " " + hour;
  }

  public int getIDTutor() {
    return idTutor;
  }

  public String getNameTutor() {
    return nameTutor;
  }

  public PeriodDTO getPeriod() {
    return period;
  }
}
