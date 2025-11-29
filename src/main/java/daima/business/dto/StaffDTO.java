package daima.business.dto;

import daima.business.enumeration.StaffRole;

public class StaffDTO extends Person {
  private final String idWorker;
  private final StaffRole role;

  public StaffDTO(String name, String lastName, String email, String idWorker, StaffRole role) {
    super(name, lastName, email);
    this.idWorker = idWorker;
    this.role = role;
  }

  public String getIDWorker() {
    return idWorker;
  }

  public StaffRole getRole() {
    return role;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", getFullName(), getEmail());
  }
}
