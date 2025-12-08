package daima.business.dto;

import daima.business.enumeration.TutoredState;
import daima.business.validator.Validator;
import daima.common.InvalidFieldException;

import java.util.Optional;

public class TutoredDTO extends Person {
  private String enrollment;
  private TutoredState state;
  private int idProgram;
  private String programName;
  private int idTutor;
  private String tutorName;

  public String getEnrollment() {
    return enrollment;
  }

  public String getFormattedEnrollment() {
    return "S".concat(enrollment);
  }

  public void setEnrollment(String enrollment) throws InvalidFieldException {
    this.enrollment = Validator.getValidEnrollment(enrollment);
    super.setEmail(String.format("zS%s@estudiantes.uv.mx", enrollment));
  }

  @Override
  public void setEmail(String email) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Email is derived from enrollment and cannot be set directly.");
  }

  public TutoredState getRiskState() {
    return state;
  }

  public void setRiskState(TutoredState state) {
    this.state = state;
  }

  public int getIDProgram() {
    return idProgram;
  }

  public void setIDProgram(int idProgram) {
    this.idProgram = idProgram;
  }

  public String getProgramName() {
    return programName;
  }

  public void setProgramName(String programName) throws InvalidFieldException {
    this.programName = Validator.getValidFlexibleName(programName, "Nombre de Programa", 3, 128);
  }

  public Optional<Integer> getIDTutor() {
    return Optional.ofNullable(idTutor == 0 ? null : idTutor);
  }

  public void setIDTutor(int idTutor) {
    this.idTutor = idTutor;
  }

  public Optional<String> getTutorName() {
    return Optional.ofNullable(tutorName);
  }

  public void setTutorName(String tutorName) throws InvalidFieldException {
    if (tutorName != null) {
      this.tutorName = Validator.getValidFlexibleName(tutorName, "Nombre de Tutor", 2, 64);
    }
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", getFullName(), getEnrollment());
  }

  @Override
  public String getSearchableText() {
    return String.format("%s %s %s %s %s",
      super.getSearchableText(),
      getTutorName(),
      getFormattedEnrollment(),
      getProgramName(),
      getRiskState().toString()
    ).toLowerCase();
  }
}
