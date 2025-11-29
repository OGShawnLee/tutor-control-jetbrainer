package daima.business.enumeration;

public enum TutoredState {
  IN_RISK,
  STABLE;

  @Override
  public String toString() {
    switch (this) {
      case IN_RISK:
        return "En Riesgo";
      case STABLE:
        return "Fuera de Peligro";
      default:
        return super.toString();
    }
  }
}
