package daima.gui.controller;

import daima.business.AuthClient;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogInController extends Controller {
  @FXML
  private Label labelTagEmail;
  @FXML
  private TextField fieldEmail;
  @FXML
  private Label labelTagPassword;
  @FXML
  private PasswordField fieldPassword;

  public void initialize() {
    cleanErrorLabels();
  }

  public void cleanErrorLabels() {
    labelTagEmail.setText("");
    labelTagPassword.setText("");
  }

  public boolean isInvalidData() {
    return true;
  }

  public void onClickLogIn() {
    AuthClient.getInstance().setCurrentStaff(
      new StaffDTO(
        "Daima",
        "Admin",
        "Admin@email.com",
        "001",
        StaffRole.ADMINISTRATOR
      )
    );
    navigateToLandingPage();
  }
}
