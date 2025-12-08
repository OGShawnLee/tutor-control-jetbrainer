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
    UpdateProfileController.displayUpdateStaffModal(this::initialize);
  }

  public void onClickLogOut() {
    AuthClient.getInstance().handleLogOut();
    navigateFromThisPageTo("Página de Inicio", "GUILogInPage");
  }
}