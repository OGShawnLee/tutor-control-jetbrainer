package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dao.ProgramDAO;
import daima.business.dao.StaffDAO;
import daima.business.dto.ProgramDTO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.business.validator.Validator;
import daima.business.validator.ValidationResult;
import daima.business.service.ProgramService;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.Optional;

public class RegisterProgramController extends Controller {
  @FXML
  private Label title;
  @FXML
  private TextField fieldAcronym;
  @FXML
  private Label labelTagAcronym;
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagName;
  @FXML
  private ComboBox<StaffDTO> fieldCoordinator;
  private ProgramDTO editProgramDTO;

  private void setContext(ProgramDTO editProgramDTO) {
    this.editProgramDTO = editProgramDTO;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldCoordinator();
  }

  private void configureTitle() {
    if (editProgramDTO == null) return;

    title.setText("Modificar Programa Educativo");
  }

  private void configureFieldCoordinator() {
    try {
      fieldCoordinator.getItems().addAll(StaffDAO.getInstance().getAllByRole(StaffRole.COORDINATOR));
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  private void loadEditData() {
    if (editProgramDTO == null) return;

    fieldName.setText(editProgramDTO.getName());
    fieldAcronym.setText(editProgramDTO.getAcronym());
    editProgramDTO.getIDCoordinator().ifPresent(idCoordinator -> {
      for (StaffDTO staffDTO : fieldCoordinator.getItems()) {
        if (staffDTO.getID() == idCoordinator) {
          fieldCoordinator.setValue(staffDTO);
          break;
        }
      }
    });
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagAcronym.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    ValidationResult<String> result = Validator.getFlexibleNameValidationResult(
      fieldName.getText(),
      "Nombre de Programa",
      3,
      128
    );
    if (result.isInvalid()) {
      labelTagName.setText(result.getError());
      isInvalidData = true;
    }

    result = Validator.getAcronymValidationResult(fieldAcronym.getText());
    if (result.isInvalid()) {
      labelTagAcronym.setText(result.getError());
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private ProgramDTO getProgramDTOFromInput() {
    ProgramDTO programDTO = this.editProgramDTO == null ? new ProgramDTO() : this.editProgramDTO;

    programDTO.setName(fieldName.getText().trim());
    programDTO.setAcronym(fieldAcronym.getText().trim());

    if (fieldCoordinator.getValue() != null) {
      programDTO.setIDCoordinator(fieldCoordinator.getValue().getID());
    }

    return programDTO;
  }

  private void handleDuplicateProgramVerification() throws UserDisplayableException {
    if (editProgramDTO == null || !editProgramDTO.getAcronym().equalsIgnoreCase(fieldAcronym.getText().trim())) {
      Optional<ProgramDTO> existingProgramDTO = ProgramDAO.getInstance().findOneByAcronym(fieldAcronym.getText().trim());

      if (existingProgramDTO.isPresent()) {
        throw new UserDisplayableException(
          "No ha sido posible registrar programa educativo debido a que ya existe uno con ese acrónimo."
        );
      }
    }
  }

  private void registerProgram() throws InvalidFieldException, UserDisplayableException {
    assert editProgramDTO == null;

    ProgramService.getInstance().createProgram(getProgramDTOFromInput());
    AlertFacade.showSuccessAndWait("El programa educativo ha sido registrado exitosamente.");
  }

  private boolean getUpdateConfirmation() {
    int oldIDCoordinator = editProgramDTO.getIDCoordinator().orElse(-1);
    int newIDCoordinator = fieldCoordinator.getValue() != null ? fieldCoordinator.getValue().getID() : -1;

    if (oldIDCoordinator == newIDCoordinator) {
      return false;
    }

    return AlertFacade.showConfirmationAndWait(
      "¿Está seguro que desea cambiar el coordinador del programa educativo?"
    );
  }

  private void updateProgram() throws InvalidFieldException, UserDisplayableException {
    assert editProgramDTO != null;

    if (getUpdateConfirmation()) {
      boolean failed = ProgramService.getInstance().updateProgram(getProgramDTOFromInput());

      if (failed) {
        AlertFacade.showErrorAndWait("No ha sido posible modificar el programa educativo.");
      } else {
        AlertFacade.showSuccessAndWait("El programa educativo ha sido modificado exitosamente.");
      }
    }
  }

  public void onClickRegisterProgram() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      handleDuplicateProgramVerification();
      if (editProgramDTO == null) {
        registerProgram();
      } else {
        updateProgram();
      }
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  public static void displayRegisterProgramModal(Runnable onClose) {
    try {
      ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Registrar Programa Educativo",
          "GUIRegisterProgramModal",
          onClose
        )
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  public static void displayManageProgramModal(Runnable onClose, ProgramDTO programDTO) {
    try {
      RegisterProgramController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Registrar Programa Educativo",
          "GUIRegisterProgramModal",
          onClose
        )
      );

      controller.setContext(programDTO);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }
}
