package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class RegisterStaffController extends Controller implements ContextController<StaffDTO> {
  @FXML
  private Label title;
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
  private ComboBox<StaffRole> fieldStaffRole;
  private StaffDTO editStaffDTO;

  @Override
  public void setContext(StaffDTO data) {
    editStaffDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldStaffRole();
  }

  private void configureTitle() {
    if (editStaffDTO == null) return;

    title.setText("Modificar Miembro de Personal");
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
    labelTagIDWorker.setText("");
    labelTagEmail.setText("");
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
    // TODO: Add Update Staff
  }

  private void registerStaff() {
    // TODO: Add Register Staff
  }

  public void onClickRegisterStaff() {
    cleanErrorLabels();
    AlertFacade.showSuccessAndWait("El miembro de personal ha sido registrado exitosamente.");
  }

  public static void displayRegisterStaffModal(Runnable onClose) {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Registrar Miembro de Personal",
        "GUIRegisterStaffModal",
        onClose
      )
    );
  }
}
