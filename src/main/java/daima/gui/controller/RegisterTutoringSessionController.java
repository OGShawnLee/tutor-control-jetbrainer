package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import daima.business.AuthClient;
import daima.business.dao.TutoredDAO;
import daima.business.dao.ProgramDAO;
import daima.business.dao.TutoringSessionDAO;
import daima.business.dao.TutoringSessionPlanDAO;
import daima.business.dto.ProgramDTO;
import daima.business.dto.StaffDTO;
import daima.business.dto.TutoredDTO;
import daima.business.dto.TutoringSessionDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.service.TutoringSessionService;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.ArrayList;

public class RegisterTutoringSessionController extends Controller {
  @FXML
  private Label title;
  @FXML
  private ComboBox<ProgramDTO> fieldProgram;
  @FXML
  private Label labelTagProgram;
  @FXML
  private TextField fieldTutoringSessionKind;
  @FXML
  private Label labelTagKind;
  @FXML
  private ComboBox<TutoredDTO> fieldTutored;
  @FXML
  private Label labelTagTutored;
  @FXML
  private TextField fieldAppointmentDate;
  @FXML
  private TextField fieldHour;
  @FXML
  private Label labelTagHour;
  private TutoringSessionDTO editTutoringSessionDTO;
  private TutoringSessionPlanDTO currentPlanDTO;

  private void setContext(TutoringSessionDTO editTutoringSessionDTO) {
    this.editTutoringSessionDTO = editTutoringSessionDTO;

    configureTitle();
    fetchAndConfigureFieldProgram();
    configureFetchAndConfigureOnProgramSelection();

    if (editTutoringSessionDTO != null) {
      fetchAndConfigureEditFormData(editTutoringSessionDTO);
    }
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void fetchAndConfigureFieldProgram() {
    StaffDTO currentTutorDTO = AuthClient.getInstance().getCurrentStaff();
    ArrayList<ProgramDTO> programDTOList = currentTutorDTO.getProgramTutoredList();

    fieldProgram.getItems().setAll(programDTOList);

    // When Tutor only has 1 Program assigned then we can configure all the state right away.
    if (programDTOList.size() == 1) {
      // Set that Program and disable the field as there is only one.
      fieldProgram.setValue(programDTOList.get(0));
      fieldProgram.setDisable(true);

      fetchAndConfigureCurrentPlanData(programDTOList.get(0));
      if (currentPlanDTO != null) {
        fetchAndConfigureFieldTutored(programDTOList.get(0), currentPlanDTO);
      }
    }
  }

  private void configurePlanDependantFieldsState(TutoringSessionPlanDTO planDTO) {
    if (planDTO == null) {
      fieldTutored.setDisable(true);
      fieldTutored.setValue(null);
      fieldTutored.getItems().clear();
      fieldAppointmentDate.setText(null);
      fieldAppointmentDate.setDisable(true);
      fieldTutoringSessionKind.setText(null);
      fieldTutoringSessionKind.setDisable(true);
      fieldHour.setText(null);
      fieldHour.setDisable(true);
    } else {
      fieldTutored.setDisable(false);
      fieldAppointmentDate.setDisable(false);
      fieldTutoringSessionKind.setDisable(false);
      fieldHour.setDisable(false);
    }
  }

  private void fetchAndConfigureCurrentPlanData(ProgramDTO programTutoredDTO) {
    try {
      TutoringSessionPlanDTO previousPlanDTO = TutoringSessionService
        .getInstance()
        .getLatestSessionPlanForRegistration(programTutoredDTO.getID());

      currentPlanDTO = previousPlanDTO;
      fieldTutoringSessionKind.setText(previousPlanDTO.getKind().toString());
      fieldAppointmentDate.setText(previousPlanDTO.getFormattedAppointmentDate());
      configurePlanDependantFieldsState(currentPlanDTO);
    } catch (UserDisplayableException e) {
      configurePlanDependantFieldsState(null);
      AlertFacade.showErrorAndWait(e);
    }
  }

  private void fetchAndConfigureFieldTutored(ProgramDTO programTutoredDTO, TutoringSessionPlanDTO currentPlanDTO) {
    if (editTutoringSessionDTO != null) {
      return;
    }

    if (currentPlanDTO == null) {
      throw new IllegalArgumentException(
        "currentPlanDTO debe ser determinado antes de encontrar los Tutored para agendar un Tutoring Session."
      );
    }

    try {
      fieldTutored.getItems().setAll(
        TutoringSessionService.getInstance().getTutoredListForRegistration(
          AuthClient.getInstance().getCurrentStaff().getID(),
          programTutoredDTO.getID(),
          currentPlanDTO.getID()
        )
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  private void configureFetchAndConfigureOnProgramSelection() {
    fieldProgram.setOnAction(e -> {
      if (fieldProgram.getValue() != null) {
        fetchAndConfigureCurrentPlanData(fieldProgram.getValue());
        if (currentPlanDTO != null) {
          fetchAndConfigureFieldTutored(fieldProgram.getValue(), currentPlanDTO);
        }
      }
    });
  }

  private void configureTitle() {
    if (editTutoringSessionDTO == null) {
      title.setText("Agendar Horario de Tutoría");
    } else {
      title.setText("Reagendar Horario de Tutoría");
    }
  }

  private void fetchAndConfigureEditFormData(TutoringSessionDTO editTutoringSessionDTO) {
    if (currentPlanDTO == null) {
      throw new IllegalArgumentException(
        "editTutoringSessionDTO debe estar definido para poder configurar los campos de edición."
      );
    }

    try {
      currentPlanDTO = TutoringSessionPlanDAO.getInstance().getOne(editTutoringSessionDTO.getIDPlan());

      TutoredDTO tutoredDTO = TutoredDAO.getInstance().getOne(editTutoringSessionDTO.getIDTutored());
      fieldTutored.getItems().setAll(tutoredDTO);
      fieldTutored.setValue(tutoredDTO);
      fieldTutored.setDisable(true);

      ProgramDTO programDTO = ProgramDAO.getInstance().getOne(tutoredDTO.getIDProgram());
      fieldProgram.getItems().setAll(programDTO);
      fieldProgram.setValue(programDTO);
      fieldProgram.setDisable(true);

      fieldTutoringSessionKind.setText(editTutoringSessionDTO.getKind().toString());
      fieldAppointmentDate.setText(editTutoringSessionDTO.getAppointmentDate().toString());
      fieldHour.setText(editTutoringSessionDTO.getHour());
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait("No ha sido posible cargar información de horario de tutoría a editar. Intente de nuevo más tarde.");
    }
  }

  private void cleanErrorLabels() {
    labelTagProgram.setText("");
    labelTagKind.setText("");
    labelTagTutored.setText("");
    labelTagHour.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    if (fieldProgram.getValue() == null) {
      labelTagTutored.setText("Por favor, seleccione un programa para configurar su horario de tutoría.");
      isInvalidData = true;
    }

    if (currentPlanDTO == null) {
      labelTagKind.setText("Por favor, seleccione un programa para configurar la sesión de tutoría.");
      isInvalidData = true;
    }

    if (fieldTutored.getValue() == null) {
      labelTagTutored.setText("Por favor, seleccione un tutorado para el horario de tutoría.");
      isInvalidData = true;
    }

    ValidationResult<String> result = Validator.getHourValidationResult(fieldHour.getText());
    if (result.isInvalid()) {
      labelTagHour.setText(result.getError());
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private TutoringSessionDTO getTutoredDTOFromInput() {
    TutoringSessionDTO sessionDTO =
      editTutoringSessionDTO == null ? new TutoringSessionDTO() : editTutoringSessionDTO;

    if (editTutoringSessionDTO == null) {
      sessionDTO.setIDTutored(fieldTutored.getValue().getID());
      sessionDTO.setIDTutor(AuthClient.getInstance().getCurrentStaff().getID());
      sessionDTO.setIDPlan(currentPlanDTO.getID());
      sessionDTO.setHour(fieldHour.getText().trim());
    } else {
      sessionDTO.setHour(fieldHour.getText().trim());
    }

    return sessionDTO;
  }

  private void scheduleTutoringSession() throws UserDisplayableException {
    TutoringSessionDAO.getInstance().createOne(getTutoredDTOFromInput());
    AlertFacade.showSuccessAndWait("Se ha agendado el horario de tutoría exitosamente.");
  }

  private void rescheduleTutoringSession() throws UserDisplayableException {
    TutoringSessionDAO.getInstance().updateOne(getTutoredDTOFromInput());
    AlertFacade.showSuccessAndWait("Se ha reagendado el horario de tutoría exitosamente.");
  }

  public void onClickRegisterTutoringSession() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      if (editTutoringSessionDTO == null) {
        scheduleTutoringSession();
      } else {
        rescheduleTutoringSession();
      }
    } catch (UserDisplayableException e) {
      AlertFacade.showSuccessAndWait(e.getMessage());
    }
  }

  public static void displayRegisterTutoringSessionModal(Runnable onClose) {
    try {
      RegisterTutoringSessionController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Agendar Horario de Tutoría",
          "GUIRegisterTutoringSessionModal",
          onClose
        )
      );

      controller.setContext(null);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  public static void displayUpdateTutoringSessionModal(Runnable onClose, TutoringSessionDTO sessionDTO) {
    try {
      TutoringSessionService.getInstance().handleCanUpdateTutoringSessionVerification(sessionDTO);

      RegisterTutoringSessionController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Reagendar Horario de Tutoría",
          "GUIRegisterTutoringSessionModal",
          onClose
        )
      );

      controller.setContext(sessionDTO);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }
}
