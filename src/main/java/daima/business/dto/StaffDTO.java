package daima.business.dto;

import org.mindrot.jbcrypt.BCrypt;

import daima.business.enumeration.StaffRole;
import daima.business.validator.Validator;
import daima.common.InvalidFieldException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StaffDTO extends Person {
  private String idWorker;
  private ArrayList<StaffRole> roles;
  private ProgramDTO programCoordinated;
  private ArrayList<ProgramDTO> programTutoredList;
  private String password;

  public String getIDWorker() {
    return idWorker;
  }

  public void setIDWorker(String workerID) throws InvalidFieldException {
    this.idWorker = Validator.getValidWorkerID(workerID);
  }

  public void setRoles(String roles) {
    if (roles == null) {
      this.roles = new ArrayList<>();
      return;
    }

    this.roles = Arrays.stream(roles.split(",")).map(it -> StaffRole.valueOf(it.trim())).collect(Collectors.toCollection(ArrayList::new));
  }

  public void setRoles(ArrayList<StaffRole> roles) {
    this.roles = roles;
  }

  public ArrayList<StaffRole> getRoles() {
    return roles;
  }

  public String getFormattedRoles() {
    return roles.stream().map(StaffRole::toString).collect(Collectors.joining(", "));
  }

  public ProgramDTO getProgramCoordinated() {
    return programCoordinated;
  }

  public void setCoordinatedProgram(ProgramDTO programCoordinated) {
    this.programCoordinated = programCoordinated;
  }

  public ArrayList<ProgramDTO> getProgramTutoredList() {
    return programTutoredList;
  }

  public void setProgramTutoredList(ArrayList<ProgramDTO> programTutoredList) {
    this.programTutoredList = programTutoredList;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) throws InvalidFieldException {
    this.password = Validator.getValidStrongPassword(password);
  }

  public void setEncryptedPasswordFromDB(String password) {
    this.password = password;
  }

  public boolean hasPasswordMatch(String candidate) {
    return BCrypt.checkpw(candidate, this.password);
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", getFullName(), getEmail());
  }

  @Override
  public String getSearchableText() {
    return String.format("%s %s %s",
      super.getSearchableText(),
      getFormattedRoles(),
      getIDWorker()
    ).toLowerCase();
  }
}
