package daima.business.dto;

import daima.business.enumeration.StaffRole;

public class StaffDTO {
  private final String email;
  private final StaffRole role;

  public StaffDTO(String email, StaffRole role) {
    this.email = email;
    this.role = role;
  }

  public String getEmail() {
    return email;
  }

  public StaffRole getRole() {
    return role;
  }
}
