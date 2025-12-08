package business.rules;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;

public class ValidatorTest {
  private static final String[] WHITESPACE_STRINGS = {
    " ",
    "     ",
    "\t",
    "\n",
    "\r\n",
    " \t\n\r "
  };

  @Test
  public void testIsEmptyString() {
    Assertions.assertTrue(Validator.isEmptyString(""));
  }

  @Test
  public void testIsEmptyStringWithWhiteSpace() {
    for (String str : WHITESPACE_STRINGS) {
      Assertions.assertTrue(Validator.isEmptyString(str));
    }
  }

  @Test
  public void testIsEmptyStringWithNull() {
    Assertions.assertTrue(Validator.isEmptyString(null));
  }

  @Test
  public void testIsEmptyStringWithNonEmptyString() {
    Assertions.assertFalse(Validator.isEmptyString("Hello, World!"));
  }

  @Test
  public void testIsStringInRange() {
    Assertions.assertTrue(Validator.isStringInRange("Hello", 3, 10));
  }

  @Test
  public void testIsStringInRangeWithTooShortString() {
    Assertions.assertFalse(Validator.isStringInRange("Hi", 3, 10));
  }

  @Test
  public void testIsStringInRangeWithTooLongString() {
    Assertions.assertFalse(Validator.isStringInRange("Hello, World!", 3, 10));
  }

  @Test
  public void testIsStringInRangeWithNull() {
    Assertions.assertFalse(Validator.isStringInRange(null, 3, 10));
  }

  @Test
  public void testIsStringInRangeWithEmptyString() {
    Assertions.assertFalse(Validator.isStringInRange("", 3, 10));
  }

  @Test
  public void testIsStringInRangeWithWhiteSpace() {
    for (String str : WHITESPACE_STRINGS) {
      Assertions.assertFalse(Validator.isStringInRange(str, 3, 10));
    }
  }

  @Test
  public void testGetEmailValidationResult() {
    ValidationResult<String> res = Validator.getEmailValidationResult("OGSmith@email.com");
    Assertions.assertFalse(res.isInvalid());
  }

  @Test
  public void testGetEmailValidationResultWithInvalidEmail() {
    ValidationResult<String> res = Validator.getEmailValidationResult("invalid-email");
    Assertions.assertTrue(res.isInvalid());
    Assertions.assertEquals("Correo electrónico debe tener el formato correcto.", res.getError());
  }

  @Test
  public void testGetEmailValidationResultWithEmptyEmail() {
    ValidationResult<String> res = Validator.getEmailValidationResult("");
    Assertions.assertTrue(res.isInvalid());
    Assertions.assertEquals("Correo electrónico no puede estar vacío.", res.getError());
  }

  @Test
  public void testGetEmailValidationResultWithNullEmail() {
    ValidationResult<String> res = Validator.getEmailValidationResult(null);
    Assertions.assertTrue(res.isInvalid());
    Assertions.assertEquals("Correo electrónico no puede estar vacío.", res.getError());
  }

  @Test
  public void testGetEmailValidationResultWithWhiteSpaceEmail() {
    for (String str : WHITESPACE_STRINGS) {
      ValidationResult<String> res = Validator.getEmailValidationResult(str);
      Assertions.assertTrue(res.isInvalid());
      Assertions.assertEquals("Correo electrónico no puede estar vacío.", res.getError());
    }
  }

  @Test
  public void testGetHourValidationResult() {
    ValidationResult<String> res = Validator.getHourValidationResult("14:30");
    Assertions.assertFalse(res.isInvalid());
  }

  @Test
  public void testGetHourValidationResultWithInvalidHour() {
    ValidationResult<String> res = Validator.getHourValidationResult("25:00");
    Assertions.assertTrue(res.isInvalid());
    Assertions.assertEquals("Hora debe estar en formato HH:MM de 24 horas.", res.getError());
  }

  @Test
  public void testGetHourValidationResultWithEmptyHour() {
    ValidationResult<String> res = Validator.getHourValidationResult("");
    Assertions.assertTrue(res.isInvalid());
    Assertions.assertEquals("Hora no puede estar vacia.", res.getError());
  }

  @Test
  public void testGetHourValidationResultWithNullHour() {
    ValidationResult<String> res = Validator.getHourValidationResult(null);
    Assertions.assertTrue(res.isInvalid());
    Assertions.assertEquals("Hora no puede estar vacia.", res.getError());
  }

  @Test
  public void testGetHourValidationResultWithWhiteSpaceHour() {
    for (String str : WHITESPACE_STRINGS) {
      ValidationResult<String> res = Validator.getHourValidationResult(str);
      Assertions.assertTrue(res.isInvalid());
      Assertions.assertEquals("Hora no puede estar vacia.", res.getError());
    }
  }

  @Test
  public void testGetHourValidationResultWithMalformedHour() {
    ValidationResult<String> res = Validator.getHourValidationResult("9 AM");
    Assertions.assertTrue(res.isInvalid());
    Assertions.assertEquals("Hora debe estar en formato HH:MM de 24 horas.", res.getError());
  }
}
