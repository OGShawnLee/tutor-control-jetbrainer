package daima.business.dto;

import daima.business.enumeration.TutoredState;

import java.util.Optional;

public class TutoredDTO extends Person {
  private final String enrollment;
  private final TutoredState state;
  private final int idProgram;
  private final String programName;
  private final int idTutor;
  private final String tutorName;

  public TutoredDTO(String name, String lastName, String email, String enrollment, int idProgram) {
    super(name, lastName, email);
    this.enrollment = enrollment;
    this.state = TutoredState.STABLE;
    this.idProgram = idProgram;
    this.programName = "LIS (Ingeniería de Software)";
    this.idTutor = 1;
    this.tutorName = "Magnus Carlsen";
  }

  public String getEnrollment() {
    return "S".concat(enrollment);
  }

  public TutoredState getState() {
    return state;
  }

  public int getIDProgram() {
    return idProgram;
  }

  public String getProgramName() {
    return programName;
  }

  public Optional<Integer> getIDTutor() {
    return Optional.ofNullable(idTutor == 0 ? null : idTutor);
  }

  public String getTutorName() {
    return tutorName;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", getFullName(), getEnrollment());
  }
}
