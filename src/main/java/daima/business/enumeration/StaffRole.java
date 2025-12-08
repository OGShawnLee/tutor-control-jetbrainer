package daima.business.enumeration;

public enum StaffRole {
  ADMIN,
  COORDINATOR,
  TUTOR,
  SUPERVISOR;

  @Override
  public String toString() {
    switch (this) {
      case ADMIN:
        return "Administrador";
      case COORDINATOR:
        return "Coordinador";
      case TUTOR:
        return "Tutor";
      case SUPERVISOR:
        return "Supervisor";
      default:
        return super.toString();
    }
  }
}
