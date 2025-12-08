package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import daima.business.AuthClient;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.business.service.AuthService;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

enum LoginState {
  PRE_LOGIN,
  ROLE_SELECTION
}

public class LogInController extends Controller {
  @FXML
  private Label labelTagEmail;
  @FXML
  private TextField fieldEmail;
  @FXML
  private Label labelTagPassword;
  @FXML
  private PasswordField fieldPassword;
  @FXML
  private ComboBox<StaffRole> fieldRole;
  @FXML
  private Label labelTagRole;
  private LoginState currentState = LoginState.PRE_LOGIN;
  private StaffDTO currentStaffDTO;

  public void initialize() {
    cleanErrorLabels();
  }

  private void cleanErrorLabels() {
    labelTagEmail.setText("");
    labelTagPassword.setText("");
    labelTagRole.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    ValidationResult<String> result = Validator.getEmailValidationResult(fieldEmail.getText());
    if (result.isInvalid()) {
      labelTagEmail.setText(result.getError());
      isInvalidData = true;
    }

    result = Validator.getStrongPasswordValidationResult(fieldPassword.getText());
    if (result.isInvalid()) {
      labelTagPassword.setText(result.getError());
      isInvalidData = true;
    }

    if (currentState == LoginState.ROLE_SELECTION) {
      StaffRole selectedRole = fieldRole.getValue();
      if (selectedRole == null) {
        labelTagRole.setText("Por favor, seleccione un rol para usar en el sistema.");
        isInvalidData = true;
      } else {
        labelTagRole.setText("");
      }
    }

    return isInvalidData;
  }

  public void onClickLogIn() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      if (currentState == LoginState.ROLE_SELECTION) {
        StaffRole selectedRole = fieldRole.getValue();
        AuthClient.getInstance().handleSignIn(currentStaffDTO, selectedRole);
        navigateToLandingPage();
        return;
      }

      AuthService.SignInResult result = AuthService.getInstance().handleSignIn(
        fieldEmail.getText().trim(),
        fieldPassword.getText().trim()
      );

      if (result.isRoleSelectionRequired()) {
        AlertFacade.showInformationAndWait("Por favor, seleccione su rol para usar en el sistema.");

        currentStaffDTO = result.getStaffDTO();
        currentState = LoginState.ROLE_SELECTION;
        fieldRole.getItems().setAll(result.getStaffDTO().getRoles());
        fieldEmail.setDisable(true);
        fieldPassword.setDisable(true);
        fieldRole.setDisable(false);
        return;
      }

      AuthClient.getInstance().handleSignIn(
        result.getStaffDTO(),
        result.getStaffDTO().getRoles().get(0)
      );
      navigateToLandingPage();
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }
}
