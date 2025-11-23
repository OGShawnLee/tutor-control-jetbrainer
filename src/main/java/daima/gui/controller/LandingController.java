package daima.gui.controller;

import daima.business.AuthClient;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public abstract class LandingController extends Controller {
  @FXML
  private Label labelEmail;

  public void initialize() {
    labelEmail.setText(AuthClient.getInstance().getCurrentStaff().getEmail());
  }

  public void onClickManageProfile() {
    // TODO: Add Update Profile
  }

  public void onClickLogOut() {
    AuthClient.getInstance().setCurrentStaff(null);
    navigateFromThisPageTo("Página de Inicio", "GUILogInPage");
  }
}