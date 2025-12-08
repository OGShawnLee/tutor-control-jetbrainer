package daima.business.validator;

public class ValidationResult<T> {
  private boolean isInvalid;
  private String error;
  private T data;

  public static <T> ValidationResult<T> createFailure(String error) {
    ValidationResult<T> result = new ValidationResult<T>();
    result.isInvalid = true;
    result.error = error;
    return result;
  }

  public static <T> ValidationResult<T> createSuccess(T data) {
    ValidationResult<T> result = new ValidationResult<T>();
    result.isInvalid = false;
    result.data = data;
    return result;
  }

  public boolean isInvalid() {
    return isInvalid;
  }

  public String getError() {
    return error;
  }

  public T getData() {
    return data;
  }
}
