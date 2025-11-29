package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.TutoringSessionKind;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class RegisterTutoringSessionPlanController extends Controller {
  @FXML
  private TextField fieldKind;
  @FXML
  private TextField fieldPeriod;
  @FXML
  private DatePicker fieldAppointmentDate;
  @FXML
  private Label labelTagAppointmentDate;

  public void initialize() {
    cleanErrorLabels();
    configureFieldPeriod();
  }

  private void configureFieldKind() {
    fieldKind.setText(TutoringSessionKind.FIRST_TUTORING_SESSION.toString());
  }

  private void configureFieldPeriod() {
    fieldPeriod.setText("AUG_JAN_2025");
  }

  private void cleanErrorLabels() {
    labelTagAppointmentDate.setText("");
  }

  private boolean isInvalidData() {
    return true;
  }

  private TutoringSessionPlanDTO getTutoringSessionPlanDTOFromInput() {
    return new TutoringSessionPlanDTO(
      fieldAppointmentDate.getValue(),
      TutoringSessionKind.FIRST_TUTORING_SESSION,
      0,
      0
    );
  }

  private void registerTutoringSessionPlan() {
    // TODO: Add Register Tutoring Session Plan
  }

  public void onClickRegisterTutoringSessionPlan() {
    cleanErrorLabels();
    AlertFacade.showSuccessAndWait("La planeación de tutoría ha sido registrada exitosamente.");
  }

  public static void displayRegisterTutoringSessionPlanModal(Runnable onClose) {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Registrar Planeación de Tutoría",
        "GUIRegisterTutoringSessionPlanModal",
        onClose
      )
    );
  }
}
