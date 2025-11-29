package daima.business.enumeration;

public enum Semester {
  FEB_JUL,
  AUG_JAN;

  @Override
  public String toString() {
    switch (this) {
      case FEB_JUL:
        return "Febrero - Julio";
      case AUG_JAN:
        return "Agosto - Enero";
      default:
        return super.toString();
    }
  }
}
