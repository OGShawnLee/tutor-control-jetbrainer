package daima.gui.controller;

import org.controlsfx.control.CheckComboBox;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Optional;

import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.business.dto.ProgramDTO;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.business.service.ProgramService;
import daima.business.service.StaffService;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.business.enumeration.StaffRole;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class RegisterStaffController extends Controller {
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
  private CheckComboBox<StaffRole> fieldStaffRole;
  @FXML
  private Label labelTagStaffRole;
  @FXML
  private CheckComboBox<ProgramDTO> fieldProgramTutor;
  @FXML
  private Label labelTagProgramTutor;
  private StaffDTO editStaffDTO;

  private void setContext(ArrayList<ProgramDTO> programDTOList, StaffDTO editStaffDTO) {
    this.editStaffDTO = editStaffDTO;
    loadEditData();
    configureTitle();
    configureFieldProgram(programDTOList);
    configureFieldStaffRole();
    configureEnableFieldProgramOnRoleSelection();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureTitle() {
    if (editStaffDTO == null) {
      title.setText("Registrar Miembro de Personal");
    } else {
      title.setText("Modificar Miembro de Personal");
    }
  }

  private void loadEditData() {
    if (editStaffDTO == null) return;

    fieldName.setText(editStaffDTO.getName());
    fieldLastName.setText(editStaffDTO.getLastName());
    fieldEmail.setText(editStaffDTO.getEmail());
    fieldIDWorker.setText(editStaffDTO.getIDWorker());
  }

  private void configureEnableFieldProgramOnRoleSelection() {
    fieldStaffRole.getCheckModel().getCheckedItems().addListener(
      (InvalidationListener) observable -> {
        ObservableList<StaffRole> roles = fieldStaffRole.getCheckModel().getCheckedItems();
        fieldProgramTutor.setDisable(!roles.contains(StaffRole.TUTOR));
      }
    );
  }

  private void configureFieldProgram(ArrayList<ProgramDTO> programDTOList) {
    fieldProgramTutor.getItems().setAll(programDTOList);
  }

  private void configureFieldStaffRole() {
    fieldStaffRole.getItems().setAll(StaffRole.values());
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagLastName.setText("");
    labelTagIDWorker.setText("");
    labelTagEmail.setText("");
    labelTagStaffRole.setText("");
    labelTagProgramTutor.setText("");
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

    ObservableList<StaffRole> roles = fieldStaffRole.getCheckModel().getCheckedItems();

    if (roles.isEmpty()) {
      labelTagStaffRole.setText("Debe seleccionar al menos un rol para el miembro de personal.");
      isInvalidData = true;
    }

    if (roles.contains(StaffRole.TUTOR) && fieldProgramTutor.getCheckModel().getCheckedItems().isEmpty()) {
      labelTagProgramTutor.setText("Debe seleccionar al menos un programa educativo para el rol de tutor.");
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private StaffDTO getStaffDTOFromInput() throws InvalidFieldException {
    StaffDTO staffDTO = new StaffDTO();
    staffDTO.setName(fieldName.getText().trim());
    staffDTO.setLastName(fieldLastName.getText().trim());
    staffDTO.setEmail(fieldEmail.getText().trim());
    staffDTO.setIDWorker(fieldIDWorker.getText().trim());
    staffDTO.setRoles(
      new ArrayList<>(fieldStaffRole.getCheckModel().getCheckedItems())
    );
    staffDTO.setProgramTutoredList(
      new ArrayList<>(fieldProgramTutor.getCheckModel().getCheckedItems())
    );
    staffDTO.setPassword(StaffDAO.createDefaultPassword(fieldIDWorker.getText()));
    return staffDTO;
  }

  private void handleDuplicateStaffVerification() throws UserDisplayableException, InvalidFieldException {

    Optional<StaffDTO> existingStaffDTO = StaffDAO.getInstance().findOneByEmail(fieldEmail.getText().trim());

    if (existingStaffDTO.isPresent()) {
      throw new UserDisplayableException(
        "No ha sido posible registrar miembro de personal debido a que ya existe una cuenta con ese correo electrónico."
      );
    }

    existingStaffDTO = StaffDAO.getInstance().findOneByWorkerID(fieldIDWorker.getText().trim());

    if (existingStaffDTO.isPresent()) {
      throw new UserDisplayableException(
        "No ha sido posible registrar miembro de personal debido a que ya existe una cuenta con ese número de empleado."
      );
    }
  }

  private boolean getUpdateConfirmation() {
    String originalEmail = editStaffDTO.getEmail().trim();
    String newEmail = fieldEmail.getText().trim();

    if (!originalEmail.equals(newEmail)) {
      boolean shallUpdate = AlertFacade.showConfirmationAndWait(
        String.format(
          "¿Esta seguro de que desea cambiar el correo electrónico del miembro de personal de %s a %s?",
          originalEmail,
          newEmail
        )
      );

      if (!shallUpdate) return false;
    }

    String originalWorkerID = editStaffDTO.getIDWorker().trim();
    String newWorkerID = fieldIDWorker.getText().trim();

    if (!originalWorkerID.equals(newWorkerID)) {
      return AlertFacade.showConfirmationAndWait(
        String.format(
          "¿Esta seguro de que desea cambiar el número de empleado del miembro de personal de %s a %s?",
          originalWorkerID,
          newWorkerID
        )
      );
    }

    ArrayList<StaffRole> originalRoles = editStaffDTO.getRoles();
    ArrayList<StaffRole> newRoles = new ArrayList<>(fieldStaffRole.getCheckModel().getCheckedItems());

    if (!originalRoles.equals(newRoles)) {
      return AlertFacade.showConfirmationAndWait(
        "¿Esta seguro de que desea cambiar los roles del miembro de personal?"
      );
    }

    return true;
  }

  private void registerStaff() throws InvalidFieldException, UserDisplayableException {
    StaffService.getInstance().createStaff(getStaffDTOFromInput());
    AlertFacade.showSuccessAndWait("El miembro de personal ha sido registrado exitosamente.");
  }

  private void updateStaff() {
    // TODO: Add Update Staff
  }

  public void onClickRegisterStaff() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      if (editStaffDTO == null) {
        handleDuplicateStaffVerification();
        registerStaff();
      } else {
        updateStaff();
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  public static void displayManageStaffModal(Runnable onClose, StaffDTO staffDTO) {
    try {
      ArrayList<ProgramDTO> programDTOList = ProgramService.getInstance().getAllForRegistration();

      RegisterStaffController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          String.format("%s Miembro de Personal", staffDTO == null ? "Registrar" : "Modificar"),
          "GUIRegisterStaffModal",
          onClose
        )
      );

      controller.setContext(programDTOList, staffDTO);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  public static void displayRegisterStaffModal(Runnable onClose) {
    displayManageStaffModal(onClose, null);
  }
}
