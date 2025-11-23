package daima.gui.controller;

import daima.business.AuthClient;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
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
  private ComboBox<StaffRole> fieldStaffRole;
  @FXML
  private TextField fieldPassword;
  @FXML
  private Label labelTagPassword;
  @FXML
  private TextField fieldConfirmPassword;
  @FXML
  private Label labelTagConfirmPassword;
  private StaffDTO editStaffDTO;

  public void setContext(StaffDTO data) {
    editStaffDTO = data;
    loadEditData();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldStaffRole();
    setContext(AuthClient.getInstance().getCurrentStaff());
  }

  private void loadEditData() {
    if (editStaffDTO == null) return;

    fieldName.setText(editStaffDTO.getName());
    fieldLastName.setText(editStaffDTO.getLastName());
    fieldEmail.setText(editStaffDTO.getEmail());
    fieldIDWorker.setText(editStaffDTO.getIDWorker());
    fieldStaffRole.setValue(editStaffDTO.getRole());
  }

  private void configureFieldStaffRole() {
    fieldStaffRole.getItems().setAll(StaffRole.values());
    fieldStaffRole.setValue(StaffRole.TUTOR);
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagLastName.setText("");
    labelTagEmail.setText("");
    labelTagPassword.setText("");
    labelTagConfirmPassword.setText("");
  }

  private boolean isInvalidData() {
    return true;
  }

  private StaffDTO getStaffDTOFromInput() {
    return new StaffDTO(
      fieldName.getText(),
      fieldLastName.getText(),
      fieldEmail.getText(),
      fieldIDWorker.getText(),
      fieldStaffRole.getValue()
    );
  }

  private void updateStaff() {
    // TODO: Add Proper Update Staff
    AuthClient.getInstance().setCurrentStaff(getStaffDTOFromInput());
  }

  public void onClickUpdateProfile() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    updateStaff();
    AlertFacade.showSuccessAndWait("Su cuenta de miembro de personal ha sido actualizada exitosamente.");
  }

  public static void displayUpdateStaffModal(Runnable onClose) {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Modificar Cuenta de Personal",
        "GUIUpdateProfileModal",
        onClose
      )
    );
  }
}
