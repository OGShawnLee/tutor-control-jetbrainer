package daima.business.dto;

public class TutoredDTO extends Person {
  private final String enrollment;
  private final int idProgram;
  private String programName;

  public TutoredDTO(String name, String lastName, String email, String enrollment, int idProgram) {
    super(name, lastName, email);
    this.enrollment = enrollment;
    this.idProgram = idProgram;
  }

  public String getEnrollment() {
    return enrollment;
  }

  public int getIDProgram() {
    return idProgram;
  }

  public String getProgramName() {
    return programName;
  }
}
