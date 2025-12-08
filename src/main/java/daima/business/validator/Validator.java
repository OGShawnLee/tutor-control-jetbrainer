package daima.business.validator;

import daima.common.InvalidFieldException;

public class Validator {
  private static final String ACRONYM_REGEX = "^[A-Z]{2,6}$";
  private static final String ENROLLMENT_REGEX = "^[0-9]{8}$";
  private static final String NAME_REGEX_SPANISH = "^[A-Za-zÑñÁáÉéÍíÓóÚúÜü\\s]+$";
  private static final String EMAIL_REGEX = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
  private static final String FLEXIBLE_NAME_REGEX = "^[A-Za-zÑñÁáÉéÍíÓóÚúÜü0-9\\s\\-_/.:]+$";
  private static final String HOUR_REGEX = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
  private static final String WORKER_ID_REGEX = "^(?!0+$)[0-9]{1,5}$";
  // MIN 8 CHARS, MAX 64, AT LEAST ONE UPPERCASE, ONE LOWERCASE, ONE DIGIT, ONE SPECIAL CHAR (!@#$%^&*-+)
  private static final String PASSWORD_REGEX =
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*\\-+])[A-Za-z\\d!@#$%^&*\\-+]{8,64}$";

  public static boolean isEmptyString(String str) {
    return str == null || str.trim().isEmpty();
  }

  public static boolean isStringInRange(String str, int minLength, int maxLength) {
    if (isEmptyString(str)) {
      return false;
    }

    int length = str.trim().length();
    return length >= minLength && length <= maxLength;
  }

  public static ValidationResult<String> getEmailValidationResult(String email) {
    if (isEmptyString(email)) {
      return ValidationResult.createFailure("Correo electrónico no puede estar vacío.");
    }

    if (email.trim().matches(EMAIL_REGEX)) {
      return ValidationResult.createSuccess(email.trim());
    }

    return ValidationResult.createFailure("Correo electrónico debe tener el formato correcto.");
  }

  public static String getValidEmail(String email) throws InvalidFieldException {
    ValidationResult<String> result = getEmailValidationResult(email);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getError(), "email");
    }

    return result.getData();
  }

  public static ValidationResult<String> getFlexibleNameValidationResult(String name, String fieldName, int minLength, int maxLength) {
    if (isEmptyString(name)) {
      return ValidationResult.createFailure(String.format("%s no puede estar vacio.", fieldName));
    }

    if (!isStringInRange(name, minLength, maxLength)) {
      return ValidationResult.createFailure(
        String.format("%s debe tener entre %d y %d caracteres.", fieldName, minLength, maxLength)
      );
    }

    if (name.trim().matches(FLEXIBLE_NAME_REGEX)) {
      return ValidationResult.createSuccess(name.trim());
    }

    return ValidationResult.createFailure(String.format("%s debe contener caracteres validos.", fieldName));
  }

  public static String getValidFlexibleName(String name, String fieldName, int minLength, int maxLength) throws InvalidFieldException {
    ValidationResult<String> result = getFlexibleNameValidationResult(name, fieldName, minLength, maxLength);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getError(), "name");
    }

    return result.getData();
  }

  public static ValidationResult<String> getEnrollmentValidationResult(String enrollment) {
    if (isEmptyString(enrollment)) {
      return ValidationResult.createFailure("Matrícula no puede estar vacio.");
    }

    if (enrollment.trim().matches(ENROLLMENT_REGEX)) {
      return ValidationResult.createSuccess(enrollment.trim());
    }

    return ValidationResult.createFailure("Matrícula debe contener 8 dígitos.");
  }

  public static String getValidEnrollment(String enrollment) throws InvalidFieldException {
    ValidationResult<String> result = getEnrollmentValidationResult(enrollment);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getError(), "enrollment");
    }

    return result.getData();
  }

  public static ValidationResult<String> getNameValidationResult(String name, String fieldName, int minLength, int maxLength) {
    if (isEmptyString(name)) {
      return ValidationResult.createFailure(String.format("%s no puede estar vacio.", fieldName));
    }

    if (!isStringInRange(name, minLength, maxLength)) {
      return ValidationResult.createFailure(
        String.format("%s debe tener entre %d y %d caracteres.", fieldName, minLength, maxLength)
      );
    }

    if (name.trim().matches(NAME_REGEX_SPANISH)) {
      return ValidationResult.createSuccess(name.trim());
    }

    return ValidationResult.createFailure(String.format("%s debe contener solo letras y espacios.", fieldName));
  }

  public static String getValidName(String name, String fieldName, int minLength, int maxLength) throws InvalidFieldException {
    ValidationResult<String> result = getNameValidationResult(name, fieldName, minLength, maxLength);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getError(), "name");
    }

    return result.getData();
  }

  public static ValidationResult<String> getAcronymValidationResult(String acronym) {
    if (isEmptyString(acronym)) {
      return ValidationResult.createFailure("Acrónimo no puede estar vacio.");
    }

    if (!isStringInRange(acronym, 2, 6)) {
      return ValidationResult.createFailure("Acrónimo debe tener entre 2 y 6 caracteres.");
    }

    if (acronym.trim().matches(ACRONYM_REGEX)) {
      return ValidationResult.createSuccess(acronym.trim());
    }

    return ValidationResult.createFailure("Acrónimo debe contener 2-6 letras mayusculas.");
  }

  public static String getValidAcronym(String acronym) throws InvalidFieldException {
    ValidationResult<String> result = getAcronymValidationResult(acronym);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getError(), "acronym");
    }

    return result.getData();
  }

  public static ValidationResult<String> getStrongPasswordValidationResult(String password) {
    if (isEmptyString(password)) {
      return ValidationResult.createFailure("Contraseña no puede estar vacia.");
    }

    if (password.trim().matches(PASSWORD_REGEX)) {
      return ValidationResult.createSuccess(password.trim());
    }

    return ValidationResult.createFailure(
      "Contraseña debe tener entre 8 y 64 caracteres, al menos una letra mayúscula, una letra minúscula, un dígito y un carácter especial (!@#$%^&*-+)."
    );
  }

  public static String getValidStrongPassword(String password) throws InvalidFieldException {
    ValidationResult<String> result = getStrongPasswordValidationResult(password);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getError(), "password");
    }

    return result.getData();
  }

  public static ValidationResult<String> getWorkerIDValidationResult(String workerID) {
    if (isEmptyString(workerID)) {
      return ValidationResult.createFailure("Número de Trabajador no puede estar vacio.");
    }

    if (!isStringInRange(workerID, 1, 5)) {
      return ValidationResult.createFailure("Número de Trabajador debe tener entre 1 y 5 caracteres.");
    }

    if (workerID.trim().matches(WORKER_ID_REGEX)) {
      return ValidationResult.createSuccess(
        workerID.replaceFirst("^0+(?!$)", "").trim()
      );
    }

    return ValidationResult.createFailure("Número de Trabajador debe contener 1-5 digitos, y no puede contener únicamente ceros.");
  }

  public static String getValidWorkerID(String workerID) throws InvalidFieldException {
    ValidationResult<String> result = getWorkerIDValidationResult(workerID);

    if (result.isInvalid()) {
      throw new InvalidFieldException(result.getError(), "worker-id");
    }

    return result.getData();
  }

  public static ValidationResult<String> getHourValidationResult(String hour) {
    if (isEmptyString(hour)) {
      return ValidationResult.createFailure("Hora no puede estar vacia.");
    }

    if (hour.trim().matches(HOUR_REGEX)) {
      return ValidationResult.createSuccess(hour.trim());
    }

    return ValidationResult.createFailure("Hora debe estar en formato HH:MM de 24 horas.");
  }

  public static ValidationResult<String> getContentValidationResult(String content) {
    if (isEmptyString(content)) {
      return ValidationResult.createFailure("Contenido no puede estar vacio.");
    }

    if (isStringInRange(content, 64, 1024)) {
      return ValidationResult.createSuccess(content);
    }

    return ValidationResult.createFailure("Contenido debe tener entre 64 y 1024 caracteres.");
  }
}
