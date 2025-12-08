package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dao.TutoredDAO;
import daima.business.dto.ProgramDTO;
import daima.business.dto.TutoredDTO;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.business.service.ProgramService;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.ArrayList;
import java.util.Optional;

public class RegisterTutoredController extends Controller {
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
  private TextField fieldEnrollment;
  @FXML
  private Label labelTagEnrollment;
  @FXML
  private ComboBox<ProgramDTO> fieldProgram;
  @FXML
  private Label labelTagProgram;
  private TutoredDTO editTutoredDTO;

  public void setContext(ArrayList<ProgramDTO> programDTOList, TutoredDTO editTutoredDTO) {
    this.editTutoredDTO = editTutoredDTO;
    configureFieldProgram(programDTOList);
    configureEditFormData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureTitle() {
    if (editTutoredDTO == null) {
      title.setText("Registrar Tutorado");
    } else {
      title.setText("Modificar Tutorado");
    }
  }

  private void configureEditFormData() {
    if (editTutoredDTO == null) return;

    fieldName.setText(editTutoredDTO.getName());
    fieldLastName.setText(editTutoredDTO.getLastName());
    fieldEnrollment.setText(editTutoredDTO.getEnrollment());
    configureEditFormDataProgramField();
  }

  private void configureEditFormDataProgramField() {
    if (editTutoredDTO.getIDTutor().isPresent()) {
      fieldProgram.setDisable(true);
    }

    for (ProgramDTO programDTO : fieldProgram.getItems()) {
      if (programDTO.getID() == editTutoredDTO.getIDProgram()) {
        fieldProgram.setValue(programDTO);
        return;
      }
    }
  }

  private void configureFieldProgram(ArrayList<ProgramDTO> programDTOList) {
    fieldProgram.getItems().setAll(programDTOList);
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagLastName.setText("");
    labelTagEnrollment.setText("");
    labelTagProgram.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalid = false;

    ValidationResult<String> nameResult = Validator.getNameValidationResult(
      fieldName.getText(), "Nombre", 3, 64
    );
    if (nameResult.isInvalid()) {
      labelTagName.setText(nameResult.getError());
      isInvalid = true;
    }

    ValidationResult<String> lastNameResult = Validator.getNameValidationResult(
      fieldLastName.getText(), "Apellido", 3, 256
    );
    if (lastNameResult.isInvalid()) {
      labelTagLastName.setText(lastNameResult.getError());
      isInvalid = true;
    }

    ValidationResult<String> enrollmentResult = Validator.getEnrollmentValidationResult(fieldEnrollment.getText());
    if (enrollmentResult.isInvalid()) {
      labelTagEnrollment.setText(enrollmentResult.getError());
      isInvalid = true;
    }

    if (fieldProgram.getValue() == null) {
      labelTagProgram.setText("Debe seleccionar un Programa Educativo.");
      isInvalid = true;
    }

    return isInvalid;
  }

  private TutoredDTO getTutoredDTOFromInput() throws InvalidFieldException {
    if (editTutoredDTO == null) {
      TutoredDTO tutoredDTO = new TutoredDTO();
      tutoredDTO.setName(fieldName.getText().trim());
      tutoredDTO.setLastName(fieldLastName.getText().trim());
      tutoredDTO.setEnrollment(fieldEnrollment.getText().trim());
      tutoredDTO.setIDProgram(fieldProgram.getValue().getID());
      return tutoredDTO;
    } else {
      editTutoredDTO.setName(fieldName.getText().trim());
      editTutoredDTO.setIDProgram(fieldProgram.getValue().getID());
      editTutoredDTO.setLastName(fieldLastName.getText().trim());
      editTutoredDTO.setEnrollment(fieldEnrollment.getText().trim());
      return editTutoredDTO;
    }
  }

  private void handleDuplicateTutoredVerification() throws UserDisplayableException {
    if (editTutoredDTO == null || !editTutoredDTO.getEnrollment().equals(fieldEnrollment.getText())) {
      Optional<TutoredDTO> existingTutoredDTO = TutoredDAO.getInstance().findOneByEnrollment(fieldEnrollment.getText());

      if (existingTutoredDTO.isPresent()) {
        labelTagEnrollment.setText("La matrícula le pertenece a otro tutorado.");
        throw new UserDisplayableException("No ha sido posible registrar tutorado por que la matricula introducida ya se encuentra en uso.");
      }
    }
  }

  private boolean getUpdateConfirmation() {
    String originalEnrollment = editTutoredDTO.getEnrollment();
    String newEnrollment = fieldEnrollment.getText();

    if (!originalEnrollment.equals(newEnrollment)) {
      boolean shallUpdate = AlertFacade.showConfirmationAndWait(
        String.format(
          "¿Esta seguro de que desea cambiar la matrícula del tutorado de %s a %s?",
          originalEnrollment,
          newEnrollment
        )
      );

      if (!shallUpdate) return false;
    }

    int originalProgramID = editTutoredDTO.getIDProgram();
    if (originalProgramID != fieldProgram.getValue().getID()) {
      return AlertFacade.showConfirmationAndWait(
        String.format(
          "¿Esta seguro de que desea cambiar el programa educativo del tutorado de %s a %s?",
          editTutoredDTO.getProgramName(),
          fieldProgram.getValue().getName()
        )
      );
    }

    return true;
  }

  private void registerTutored() throws InvalidFieldException, UserDisplayableException {
    TutoredDAO.getInstance().createOne(getTutoredDTOFromInput());
    AlertFacade.showSuccessAndWait("El tutorado ha sido registrado exitosamente.");
    close();
  }

  private void updateTutored() throws InvalidFieldException, UserDisplayableException {
    assert editTutoredDTO != null;

    if (getUpdateConfirmation()) {
      boolean failed = TutoredDAO.getInstance().updateOne(getTutoredDTOFromInput());

      if (failed) {
        AlertFacade.showErrorAndWait("No ha sido posible modificar el tutorado.");
      } else {
        AlertFacade.showSuccessAndWait("El tutorado ha sido modificado exitosamente.");
        close();
      }
    }
  }

  public void onClickRegisterStaff() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      handleDuplicateTutoredVerification();
      if (editTutoredDTO == null) registerTutored();
      else updateTutored();
    } catch (InvalidFieldException | UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  public static void displayManageTutoredModal(Runnable onClose, TutoredDTO tutoredDTO) {
    try {
      ArrayList<ProgramDTO> programDTOList = ProgramService.getInstance().getAllForRegistration();

      RegisterTutoredController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          String.format("%s Tutorado", tutoredDTO == null ? "Registrar" : "Modificar"),
          "GUIRegisterTutoredModal",
          onClose
        )
      );

      controller.setContext(programDTOList, tutoredDTO);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  public static void displayRegisterTutoredModal(Runnable onClose) {
    displayManageTutoredModal(onClose, null);
  }
}
