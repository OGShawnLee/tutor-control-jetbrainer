package daima.business.enumeration;

public enum ReportState {
  DRAFT,
  REVIEWED,
  SENT;

  @Override
  public String toString() {
    switch (this) {
      case DRAFT:
        return "Borrador";
      case REVIEWED:
        return "Revisado";
      case SENT:
        return "Enviado";
      default:
        return super.toString();
    }
  }
}
