package daima.business.enumeration;

public enum TutoringSessionPlanState {
  SCHEDULED,
  COMPLETED;

  @Override
  public String toString() {
    switch (this) {
      case SCHEDULED:
        return "Programada";
      case COMPLETED:
        return "Concluida";
      default:
        return super.toString();
    }
  }
}