package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.business.service.StaffService;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

public class SignUpController extends Controller {
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagName;
  @FXML
  private TextField fieldLastName;
  @FXML
  private Label labelTagLastName;
  @FXML
  private TextField fieldEmail;
  @FXML
  private Label labelTagEmail;
  @FXML
  private TextField fieldIDWorker;
  @FXML
  private Label labelTagIDWorker;
  @FXML
  private PasswordField fieldPassword;
  @FXML
  private Label labelTagPassword;
  @FXML
  private PasswordField fieldConfirmPassword;
  @FXML
  private Label labelTagConfirmPassword;

  public void initialize() {
    displayInstructions();
    cleanErrorLabels();
  }

  public void displayInstructions() {
    AlertFacade.showInformationAndWait("Bienvenido a su Sistema de Control Interno de Tutorías.");
    AlertFacade.showInformationAndWait("Por favor, cree su cuenta de administrador para iniciar el sistema.");
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagLastName.setText("");
    labelTagIDWorker.setText("");
    labelTagEmail.setText("");
    labelTagPassword.setText("");
    labelTagConfirmPassword.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    ValidationResult<String> result = Validator.getNameValidationResult(
      fieldName.getText(),
      "Nombre",
      3,
      64
    );
    if (result.isInvalid()) {
      labelTagName.setText(result.getError());
      isInvalidData = true;
    }

    result = Validator.getNameValidationResult(
      fieldLastName.getText(),
      "Apellidos",
      3,
      256
    );
    if (result.isInvalid()) {
      labelTagLastName.setText(result.getError());
      isInvalidData = true;
    }

    result = Validator.getEmailValidationResult(fieldEmail.getText());
    if (result.isInvalid()) {
      labelTagEmail.setText(result.getError());
      isInvalidData = true;
    }

    result = Validator.getWorkerIDValidationResult(fieldIDWorker.getText());
    if (result.isInvalid()) {
      labelTagIDWorker.setText(result.getError());
      isInvalidData = true;
    }

    ValidationResult<String> passwordResult = Validator.getStrongPasswordValidationResult(fieldPassword.getText());
    if (passwordResult.isInvalid()) {
      labelTagPassword.setText(passwordResult.getError());
      isInvalidData = true;
    }

    ValidationResult<String> confirmPasswordResult = Validator.getStrongPasswordValidationResult(fieldConfirmPassword.getText());
    if (confirmPasswordResult.isInvalid()) {
      labelTagConfirmPassword.setText(confirmPasswordResult.getError());
      isInvalidData = true;
    }

    if (!passwordResult.isInvalid() && !confirmPasswordResult.isInvalid()) {
      if (!fieldPassword.getText().equals(fieldConfirmPassword.getText())) {
        labelTagConfirmPassword.setText("Las contraseñas no coinciden.");
        isInvalidData = true;
      }
    }

    return isInvalidData;
  }

  private StaffDTO getStaffDTOFromInput() throws InvalidFieldException {
    StaffDTO staffDTO = new StaffDTO();
    staffDTO.setName(fieldName.getText().trim());
    staffDTO.setLastName(fieldLastName.getText().trim());
    staffDTO.setEmail(fieldEmail.getText().trim());
    staffDTO.setIDWorker(fieldIDWorker.getText().trim());
    staffDTO.setPassword(fieldPassword.getText().trim());
    staffDTO.setRoles(
      new ArrayList<>(Collections.singletonList(StaffRole.ADMIN))
    );
    return staffDTO;
  }

  private void handleDuplicateStaffVerification() throws UserDisplayableException {
    Optional<StaffDTO> existingStaffDTO = StaffDAO.getInstance().findOneByEmail(fieldEmail.getText());

    if (existingStaffDTO.isPresent()) {
      throw new UserDisplayableException("No ha sido posible registrar administrador debido a que ya existe una cuenta con ese correo electrónico.");
    }

    existingStaffDTO = StaffDAO.getInstance().findOneByWorkerID(fieldIDWorker.getText());

    if (existingStaffDTO.isPresent()) {
      throw new UserDisplayableException("No ha sido posible registrar administrador debido a que ya existe una cuenta con ese número de empleado.");
    }
  }

  public void registerAdmin() throws InvalidFieldException, UserDisplayableException {
    StaffService.getInstance().createStaff(getStaffDTOFromInput());
    AlertFacade.showSuccessAndWait("La cuenta de administrador ha sido creada exitosamente.");
    AlertFacade.showSuccessAndWait("Ahora puede iniciar sesión con sus credenciales.");
    navigateFromThisPageTo("Login Page", "GUILoginPage");
  }

  public void onClickSignUp() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      handleDuplicateStaffVerification();
      registerAdmin();
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }
}
