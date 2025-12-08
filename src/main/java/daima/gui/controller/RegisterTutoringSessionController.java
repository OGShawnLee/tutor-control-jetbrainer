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
import daima.business.enumeration.TutoringSessionPlanState;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.ArrayList;
import java.util.Optional;

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
    fetchAndConfigureEditFormData();
  }

  public void initialize() {
    cleanErrorLabels();
    fetchAndConfigureFieldProgram();
    configureFetchAndConfigureOnProgramSelection();
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
      fetchAndConfigureFieldTutored(programDTOList.get(0));
      fetchAndConfigureCurrentPlanData(programDTOList.get(0));
    }
  }

  private void fetchAndConfigureCurrentPlanData(ProgramDTO programTutoredDTO) {
    try {
      Optional<TutoringSessionPlanDTO> previousPlanDTO = TutoringSessionPlanDAO.getInstance().findLatestByProgram(
        programTutoredDTO.getID()
      );

      if (previousPlanDTO.isPresent()) {
        if (previousPlanDTO.get().getState() == TutoringSessionPlanState.COMPLETED) {
          AlertFacade.showErrorAndWait(
            "No es posible agendar un horario debido a que aún no hay ninguna planeación de tutoría vigente para el programa educativo seleccionado."
          );
        } else {
          currentPlanDTO = previousPlanDTO.get();
          fieldTutoringSessionKind.setText(previousPlanDTO.get().getKind().toString());
          fieldAppointmentDate.setText(previousPlanDTO.get().getFormattedAppointmentDate());
        }
      } else {
        AlertFacade.showErrorAndWait(
          "No es posible agendar horario de tutoría debido a que aún no hay ninguna planeación de tutoría para el programa educativo seleccionado."
        );
      }

    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible cargar la información de la planeación de tutoría."
      );
    }
  }

  private void fetchAndConfigureFieldTutored(ProgramDTO programTutoredDTO) {
    try {
      StaffDTO currentTutorDTO = AuthClient.getInstance().getCurrentStaff();
      ArrayList<TutoredDTO> tutoredDTOList = TutoredDAO.getInstance().getAllByProgramAndTutor(
        programTutoredDTO.getID(),
        currentTutorDTO.getID()
      );

      if (tutoredDTOList.isEmpty()) {
        AlertFacade.showErrorAndWait(
          "No es posible agendar horario porque no hay ningún tutorado registrado en el sistema aún para este programa educativo."
        );
      }

      fieldTutored.getItems().setAll(tutoredDTOList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde"
      );
    }
  }

  private void configureFetchAndConfigureOnProgramSelection() {
    fieldProgram.setOnAction(e -> {
      if (fieldProgram.getValue() != null) {
        fetchAndConfigureFieldTutored(fieldProgram.getValue());
        fetchAndConfigureCurrentPlanData(fieldProgram.getValue());
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

  private void fetchAndConfigureEditFormData() {
    if (editTutoringSessionDTO == null) return;

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
      ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Reagendar Horario de Tutoría",
          "GUIRegisterTutoringSessionModal",
          onClose
        )
      );
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  public static void displayUpdateTutoringSessionModal(Runnable onClose, TutoringSessionDTO sessionDTO) {
    try {
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
