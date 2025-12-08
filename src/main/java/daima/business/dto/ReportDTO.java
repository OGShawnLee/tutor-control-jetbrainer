package daima.business.dto;

import daima.business.enumeration.ReportState;
import daima.business.enumeration.ReportType;
import daima.business.enumeration.TutoringSessionKind;

import java.time.LocalDateTime;

public class ReportDTO implements Record, Searchable {
  private int id;
  private int idStaff;
  private int idSessionPlan;
  private int idProgram;
  private int idResponse;
  private String nameStaff;
  private String content;
  private TutoringSessionKind sessionKind;
  private ReportType type;
  private ReportState state;
  private PeriodDTO periodDTO;
  private LocalDateTime createdAt;

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public int getIDStaff() {
    return idStaff;
  }

  public void setIDStaff(int idStaff) {
    this.idStaff = idStaff;
  }

  public int getIDSessionPlan() {
    return idSessionPlan;
  }

  public void setIDSessionPlan(int idSessionPlan) {
    this.idSessionPlan = idSessionPlan;
  }

  public int getIDProgram() {
    return idProgram;
  }

  public void setIDProgram(int idProgram) {
    this.idProgram = idProgram;
  }

  public int getIDResponse() {
    return idResponse;
  }

  public void setIDResponse(int idResponse) {
    this.idResponse = idResponse;
  }

  public String getNameStaff() {
    return nameStaff;
  }

  public void setNameStaff(String nameStaff) {
    this.nameStaff = nameStaff;
  }

  public TutoringSessionKind getSessionKind() {
    return sessionKind;
  }

  public void setSessionKind(TutoringSessionKind sessionKind) {
    this.sessionKind = sessionKind;
  }

  public ReportType getType() {
    return type;
  }

  public void setType(ReportType type) {
    this.type = type;
  }

  public ReportState getState() {
    return state;
  }

  public void setState(ReportState state) {
    this.state = state;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

  public String getSearchableText() {
    return String.format("%s %s %s %s %s",
      getState().toString(),
      getNameStaff(),
      getPeriodDTO().toString(),
      getSessionKind().toString(),
      getFormattedCreatedAt()
    ).toLowerCase();
  }
}
