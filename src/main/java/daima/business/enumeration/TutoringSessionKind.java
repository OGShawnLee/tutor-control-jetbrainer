package daima.business.enumeration;

public enum TutoringSessionKind {
  FIRST_TUTORING_SESSION,
  SECOND_TUTORING_SESSION,
  THIRD_TUTORING_SESSION;

  @Override
  public String toString() {
    switch (this) {
      case FIRST_TUTORING_SESSION:
        return "Primera Sesión Tutoría";
      case SECOND_TUTORING_SESSION:
        return "Segunda Sesión de Tutoría";
      case THIRD_TUTORING_SESSION:
        return "Tercera Sesión de Tutoría";
      default:
        return super.toString();
    }
  }
}