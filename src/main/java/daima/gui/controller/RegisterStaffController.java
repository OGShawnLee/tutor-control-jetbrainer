package daima.gui.controller;

import org.controlsfx.control.CheckComboBox;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Optional;

import daima.business.dao.ProgramDAO;
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
  private CheckComboBox<ProgramDTO> fieldTutoredProgram;
  @FXML
  private Label labelTagProgramTutor;
  private StaffDTO editStaffDTO;

  private void setContext(ArrayList<ProgramDTO> programDTOList, StaffDTO editStaffDTO) {
    this.editStaffDTO = editStaffDTO;
    configureTitle();
    configureFieldTutoredProgram(programDTOList);
    configureFieldStaffRole();
    configureEnableFieldProgramOnRoleSelection();
    configureEditFormData();
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

  private void configureEditFormData() {
    if (editStaffDTO == null) return;

    fieldName.setText(editStaffDTO.getName());
    fieldLastName.setText(editStaffDTO.getLastName());
    fieldEmail.setText(editStaffDTO.getEmail());
    fieldIDWorker.setText(editStaffDTO.getIDWorker());

    disableImmutableFields();
    checkEditStaffDTORoles();
    checkEditStaffDTOTutoredProgramList();
  }

  private void disableImmutableFields() {
    fieldEmail.setDisable(true);
    fieldIDWorker.setDisable(true);
  }

  private void checkEditStaffDTORoles() {
    for (StaffRole role : editStaffDTO.getRoles()) {
      fieldStaffRole.getCheckModel().check(role);
    }
  }

  private void checkEditStaffDTOTutoredProgramList() {
    if (editStaffDTO.getRoles().contains(StaffRole.TUTOR)) {
      fieldTutoredProgram.getItems().forEach(it -> {
        for (ProgramDTO programDTO : editStaffDTO.getProgramTutoredList()) {
          if (it.getID() == programDTO.getID()) {
            fieldTutoredProgram.getCheckModel().check(it);
            break;
          }
        }
      });
    }
  }

  private void configureEnableFieldProgramOnRoleSelection() {
    fieldStaffRole.getCheckModel().getCheckedItems().addListener(
      (InvalidationListener) observable -> {
        ObservableList<StaffRole> roles = fieldStaffRole.getCheckModel().getCheckedItems();
        fieldTutoredProgram.setDisable(!roles.contains(StaffRole.TUTOR));
      }
    );
  }

  private void configureFieldTutoredProgram(ArrayList<ProgramDTO> programDTOList) {
    fieldTutoredProgram.getItems().setAll(programDTOList);
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

    if (editStaffDTO == null) {
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
    }

    ObservableList<StaffRole> roles = fieldStaffRole.getCheckModel().getCheckedItems();

    if (roles.isEmpty()) {
      labelTagStaffRole.setText("Debe seleccionar al menos un rol para el miembro de personal.");
      isInvalidData = true;
    }

    if (roles.contains(StaffRole.TUTOR) && fieldTutoredProgram.getCheckModel().getCheckedItems().isEmpty()) {
      labelTagProgramTutor.setText("Debe seleccionar al menos un programa educativo para el rol de tutor.");
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private StaffDTO getStaffDTOFromInput() throws InvalidFieldException {
    StaffDTO staffDTO = editStaffDTO == null ? new StaffDTO() : editStaffDTO;

    staffDTO.setName(fieldName.getText().trim());
    staffDTO.setLastName(fieldLastName.getText().trim());

    if (editStaffDTO == null) {
      staffDTO.setEmail(fieldEmail.getText().trim());
      staffDTO.setIDWorker(fieldIDWorker.getText().trim());
      staffDTO.setPassword(StaffDAO.createDefaultPassword(fieldIDWorker.getText()));
    }

    staffDTO.setRoles(
      new ArrayList<>(fieldStaffRole.getCheckModel().getCheckedItems())
    );
    staffDTO.setProgramTutoredList(
      new ArrayList<>(fieldTutoredProgram.getCheckModel().getCheckedItems())
    );

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
    return AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea modificar los datos del miembro de personal? " +
        "En caso de que el miembro de personal haya dejado de ser tutor o cambie de programa educativo, entonces todos los tutorados correspondientes se quedarán sin tutor y tendrán que ser asignados nuevamente."
    );
  }

  private void registerStaff() throws InvalidFieldException, UserDisplayableException {
    handleDuplicateStaffVerification();
    StaffService.getInstance().createStaff(getStaffDTOFromInput());
    AlertFacade.showSuccessAndWait(
      "El miembro de personal ha sido registrado exitosamente. La contraseña generada es el número de personal seguido de ‘@Password’. Es posible cambiarla"
    );
  }

  private void updateStaff() throws InvalidFieldException, UserDisplayableException {
    boolean shallUpdate = getUpdateConfirmation();

    if (shallUpdate) {
      StaffService.getInstance().updateStaff(getStaffDTOFromInput());
      AlertFacade.showSuccessAndWait("El miembro de personal ha sido actualizado exitosamente.");
    }
  }

  public void onClickRegisterStaff() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      if (editStaffDTO == null) {
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

      if (staffDTO != null && staffDTO.getRoles().contains(StaffRole.TUTOR)) {
        staffDTO.setProgramTutoredList(
          ProgramDAO.getInstance().getAllByTutor(staffDTO.getID())
        );
      }

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
