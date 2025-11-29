package daima.business.enumeration;

public enum AppointmentState {
  SCHEDULED,
  COMPLETED,
  MISSED;

  @Override
  public String toString() {
    switch (this) {
      case COMPLETED:
        return "Asistida";
      case SCHEDULED:
        return "Programada";
      case MISSED:
        return "Sin Asistencia";
      default:
        return super.toString();
    }
  }
}