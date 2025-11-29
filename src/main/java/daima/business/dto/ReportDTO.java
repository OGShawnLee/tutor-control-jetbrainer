package daima.business.dto;

import daima.business.enumeration.ReportState;
import daima.business.enumeration.ReportType;
import daima.business.enumeration.Semester;
import daima.business.enumeration.TutoringSessionKind;

import java.time.LocalDateTime;

public class ReportDTO implements Record {
  private int id;
  private String content;
  private PeriodDTO period;
  private ReportType type;
  private ReportState state;
  private TutoringSessionKind tutoringSessionKind;
  private LocalDateTime createdAt;
  private int idStaff;
  private String nameStaff;
  private int idResponse;
  private int idProgram;

  public ReportDTO() {
    this.state = ReportState.DRAFT;
    this.type = ReportType.GENERAL_REPORT;
    this.tutoringSessionKind = TutoringSessionKind.FIRST_TUTORING_SESSION;
    this.nameStaff = "Biggerton Ouncenton III";
    this.content = "Everything is fine! Nothing to worry about!";
    this.period = new PeriodDTO(2025, Semester.AUG_JAN);
    this.createdAt = LocalDateTime.now();
  }

  public int getID() {
    return id;
  }

  public String getContent() {
    return content;
  }

  public ReportType getType() {
    return type;
  }

  public ReportState getState() {
    return state;
  }

  public TutoringSessionKind getTutoringSessionKind() {
    return tutoringSessionKind;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public int getIDStaff() {
    return idStaff;
  }

  public String getNameStaff() {
    return nameStaff;
  }

  public PeriodDTO getPeriod() {
    return period;
  }

  public int getIDResponse() {
    return idResponse;
  }
}
