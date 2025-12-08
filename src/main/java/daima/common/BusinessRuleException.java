package daima.common;

public class BusinessRuleException extends UserDisplayableException {
  public BusinessRuleException(String message) {
    super(message);
  }

  public BusinessRuleException(String message, Throwable cause) {
    super(message, cause);
  }
}