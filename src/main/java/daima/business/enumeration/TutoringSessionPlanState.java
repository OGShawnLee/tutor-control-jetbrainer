package daima.business.enumeration;

public enum TutoringSessionPlanState {
  SCHEDULED,
  CONCLUDED;

  @Override
  public String toString() {
    switch (this) {
      case SCHEDULED:
        return "Programada";
      case CONCLUDED:
        return "Concluida";
      default:
        return super.toString();
    }
  }
}