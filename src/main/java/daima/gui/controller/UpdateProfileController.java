package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import daima.business.AuthClient;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class UpdateProfileController extends Controller {
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
  private TextField fieldFormattedRoles;
  @FXML
  private PasswordField fieldPassword;
  @FXML
  private Label labelTagPassword;
  @FXML
  private PasswordField fieldConfirmPassword;
  @FXML
  private Label labelTagConfirmPassword;
  private StaffDTO editStaffDTO;

  public void initialize() {
    editStaffDTO = AuthClient.getInstance().getCurrentStaff();
    cleanErrorLabels();
    configureFormData();
  }

  private void configureFormData() {
    if (editStaffDTO == null) return;

    fieldName.setText(editStaffDTO.getName());
    fieldLastName.setText(editStaffDTO.getLastName());
    fieldEmail.setText(editStaffDTO.getEmail());
    fieldIDWorker.setText(editStaffDTO.getIDWorker());
    fieldFormattedRoles.setText(editStaffDTO.getFormattedRoles());
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagLastName.setText("");
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

    if (isInvalidPassword()) {
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private boolean isInvalidPassword() {
    String passwordText = fieldPassword.getText();
    String confirmPasswordText = fieldConfirmPassword.getText();

    boolean hasPasswordError = false;
    boolean isPasswordProvided = (passwordText != null && !passwordText.trim().isEmpty());

    if (isPasswordProvided) {
      ValidationResult<String> passwordResult = Validator.getStrongPasswordValidationResult(passwordText);
      if (passwordResult.isInvalid()) {
        labelTagPassword.setText(passwordResult.getError());
        hasPasswordError = true;
      }

      ValidationResult<String> confirmPasswordResult = Validator.getStrongPasswordValidationResult(confirmPasswordText);
      if (confirmPasswordResult.isInvalid()) {
        labelTagConfirmPassword.setText(confirmPasswordResult.getError());
        hasPasswordError = true;
      }

      if (!hasPasswordError) {
        if (!passwordText.equals(confirmPasswordText)) {
          labelTagConfirmPassword.setText("Las contraseñas no coinciden.");
          hasPasswordError = true;
        }
      }
    } else {
      if (confirmPasswordText != null && !confirmPasswordText.trim().isEmpty()) {
        labelTagConfirmPassword.setText("Si no establece la contraseña, el campo de confirmación debe estar vacío.");
        hasPasswordError = true;
      }
    }

    return hasPasswordError;
  }

  private StaffDTO getStaffDTOFromInput() throws InvalidFieldException {
    editStaffDTO.setName(fieldName.getText());
    editStaffDTO.setLastName(fieldLastName.getText());
    editStaffDTO.setEmail(fieldEmail.getText());
    return editStaffDTO;
  }

  private void updateStaff() throws InvalidFieldException, UserDisplayableException {
    StaffDAO.getInstance().updateOne(getStaffDTOFromInput());
    AlertFacade.showSuccessAndWait("Su cuenta de miembro de personal ha sido actualizada exitosamente.");
  }

  public void onClickUpdateProfile() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      updateStaff();
    } catch (UserDisplayableException | InvalidFieldException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  public static void displayUpdateStaffModal(Runnable onClose) {
    try {
      ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Modificar Cuenta de Personal",
          "GUIUpdateProfileModal",
          onClose
        )
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }
}
