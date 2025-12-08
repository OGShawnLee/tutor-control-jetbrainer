package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.AuthClient;
import daima.business.dao.PeriodDAO;
import daima.business.dao.TutoringSessionPlanDAO;
import daima.business.dto.PeriodDTO;
import daima.business.dto.ProgramDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.service.TutoringSessionPlanService;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.time.LocalDate;

public class RegisterTutoringSessionPlanController extends Controller {
  private static final int MIN_DATE_DAYS_FOR_REGISTRATION = 3;
  private static final int MAX_DATE_DAYS_FOR_REGISTRATION = 7;
  @FXML
  private TextField fieldKind;
  @FXML
  private TextField fieldPeriod;
  @FXML
  private DatePicker fieldAppointmentDate;
  @FXML
  private Label labelTagAppointmentDate;
  private PeriodDTO currentPeriodDTO;
  private TutoringSessionKind currentSessionKind;
  private ProgramDTO coordinatedProgramDTO;

  private void setContext(
    TutoringSessionKind currentSessionKind,
    PeriodDTO currentPeriodDTO,
    ProgramDTO coordinatedProgramDTO
  ) {
    this.currentSessionKind = currentSessionKind;
    this.currentPeriodDTO = currentPeriodDTO;
    this.coordinatedProgramDTO = coordinatedProgramDTO;
    configureFieldKind();
    configureFieldPeriod();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureFieldKind() {
    fieldKind.setText(currentSessionKind.toString());
  }

  private void configureFieldPeriod() {
    fieldPeriod.setText(currentPeriodDTO.toString());
  }

  private void cleanErrorLabels() {
    labelTagAppointmentDate.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    if (fieldAppointmentDate.getValue() == null) {
      labelTagAppointmentDate.setText("La fecha de tutoría es obligatoria.");
      isInvalidData = true;
    } else {
      LocalDate currentDate = LocalDate.now();
      LocalDate MIN_DATE_ALLOWED = LocalDate.now().plusDays(MIN_DATE_DAYS_FOR_REGISTRATION);
      LocalDate MAX_DATE_ALLOWED = LocalDate.now().plusDays(MAX_DATE_DAYS_FOR_REGISTRATION);

      if (fieldAppointmentDate.getValue().isBefore(currentDate)) {
        labelTagAppointmentDate.setText("La fecha de tutoría no puede ser en el pasado.");
        isInvalidData = true;
      }

      if (fieldAppointmentDate.getValue().isBefore(MIN_DATE_ALLOWED)) {
        labelTagAppointmentDate.setText("La fecha de tutoría debe ser al menos dentro de 3 días.");
        isInvalidData = true;
      }

      if (fieldAppointmentDate.getValue().isAfter(MAX_DATE_ALLOWED)) {
        labelTagAppointmentDate.setText("La fecha de tutoría no puede ser mayor a 7 días a partir de hoy.");
        isInvalidData = true;
      }
    }

    return isInvalidData;
  }

  private TutoringSessionPlanDTO getTutoringSessionPlanDTOFromInput() {
    TutoringSessionPlanDTO tutoringSessionPlanDTO = new TutoringSessionPlanDTO();

    tutoringSessionPlanDTO.setIDProgram(coordinatedProgramDTO.getID());
    tutoringSessionPlanDTO.setPeriodDTO(currentPeriodDTO);
    tutoringSessionPlanDTO.setKind(currentSessionKind);
    tutoringSessionPlanDTO.setAppointmentDate(fieldAppointmentDate.getValue());

    return tutoringSessionPlanDTO;
  }

  private void registerTutoringSessionPlan() throws UserDisplayableException {
    TutoringSessionPlanDAO.getInstance().createOne(getTutoringSessionPlanDTOFromInput());
    AlertFacade.showSuccessAndWait("La planeación de tutoría ha sido registrada exitosamente.");
  }

  public void onClickRegisterTutoringSessionPlan() {
    cleanErrorLabels();

    if (isInvalidData()) {
      return;
    }

    try {
      registerTutoringSessionPlan();
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  public static void displayRegisterTutoringSessionPlanModal(Runnable onClose) {
    try {
      ProgramDTO coordinatedProgramDTO = AuthClient.getInstance().getCurrentStaff().getProgramCoordinated();
      PeriodDTO currentPeriodDTO = PeriodDAO.getInstance().getCurrentPeriod();
      TutoringSessionKind currentSessionKind = TutoringSessionPlanService
        .getInstance()
        .getCurrentSessionKindForRegistration(
          coordinatedProgramDTO.getID()
        );

      RegisterTutoringSessionPlanController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Registrar Planeación de Tutoría",
          "GUIRegisterTutoringSessionPlanModal",
          onClose
        )
      );

      controller.setContext(currentSessionKind, currentPeriodDTO, coordinatedProgramDTO);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }
}
