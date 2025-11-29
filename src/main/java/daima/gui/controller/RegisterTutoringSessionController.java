package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import daima.business.dao.TutoredDAO;
import daima.business.dto.TutoredDTO;
import daima.business.dto.TutoringSessionDTO;
import daima.business.enumeration.AppointmentState;
import daima.business.enumeration.TutoringSessionKind;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.ArrayList;

public class RegisterTutoringSessionController extends Controller implements ContextController<TutoringSessionDTO> {
  @FXML
  private Label title;
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

  @Override
  public void setContext(TutoringSessionDTO data) {
    editTutoringSessionDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldTutored();
  }

  private void configureTitle() {
    if (editTutoringSessionDTO == null) return;

    title.setText("Reagendar Horario de Tutoría");
  }

  private void loadEditData() {
    if (editTutoringSessionDTO == null) return;

    fieldAppointmentDate.setText(editTutoringSessionDTO.getAppointmentDate().toString());
    fieldHour.setText(editTutoringSessionDTO.getHour());
  }

  private void configureFieldTutored() {
    ArrayList<TutoredDTO> tutoredDTOList = TutoredDAO.getInstance().getAllByTutor(0);

    fieldTutored.getItems().setAll(tutoredDTOList);
  }

  private void cleanErrorLabels() {
    labelTagTutored.setText("");
    labelTagHour.setText("");
  }

  private boolean isInvalidData() {
    return true;
  }

  private TutoringSessionDTO getTutoredDTOFromInput() {
    return new TutoringSessionDTO(
      AppointmentState.SCHEDULED,
      TutoringSessionKind.FIRST_TUTORING_SESSION
    );
  }

  private void registerTutoringSession() {
    // TODO: Add Register Tutored
  }

  private void updateTutoringSession() {
    // TODO: Add Update Tutored
  }

  public void onClickRegisterTutoringSession() {
    cleanErrorLabels();
    AlertFacade.showSuccessAndWait("Se ha agendado el horario de tutoría exitosamente.");
  }

  public static void displayRegisterTutoringSessionModal(Runnable onClose) {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Agendar Horario de Tutoría",
        "GUIRegisterTutoringSessionModal",
        onClose
      )
    );
  }
}
