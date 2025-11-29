package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.business.dto.TutoredDTO;
import daima.business.enumeration.StaffRole;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.Optional;

public class AssignTutoredController extends Controller implements ContextController<TutoredDTO> {
  @FXML
  private Label title;
  @FXML
  private TextField fieldTutored;
  @FXML
  private ComboBox<StaffDTO> fieldTutor;
  @FXML
  private Label labelTagTutor;
  private TutoredDTO editTutoredDTO;

  @Override
  public void setContext(TutoredDTO data) {
    editTutoredDTO = data;
    loadData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldTutor();
  }

  private void configureTitle() {
    if (editTutoredDTO.getIDTutor().isPresent()) {
      title.setText("Asignar Tutorado");
    } else {
      title.setText("Reasignar Tutorado");
    }
  }

  private void loadData() {
    if (editTutoredDTO == null) return;

    fieldTutored.setText(editTutoredDTO.toString());
    editTutoredDTO.getIDTutor().flatMap(this::getTutorFromStaffID).ifPresent(fieldTutor::setValue);
  }

  private Optional<StaffDTO> getTutorFromStaffID(int idTutor) {
    return fieldTutor.getItems().stream().filter(it -> it.getID() == idTutor).findFirst();
  }

  private void configureFieldTutor() {
    fieldTutor.getItems().setAll(StaffDAO.getInstance().getAllByProgramAndRole(0, StaffRole.TUTOR));
  }

  private void cleanErrorLabels() {
    labelTagTutor.setText("");
  }

  private boolean isInvalidData() {
    return true;
  }

  private void assignTutored() {
    // TODO: Add Register Tutored
  }

  private void reassignTutored() {
    // TODO: Add Register Tutored
  }

  public void onClickAssignTutored() {
    cleanErrorLabels();
    AlertFacade.showSuccessAndWait("El tutorado ha sido asignado exitosamente.");
  }

  public static void displayAssignTutoredModal(TutoredDTO tutoredDTO, Runnable onClose) {
    ModalFacade.createAndDisplayContextModal(
      new ModalFacadeConfiguration(
        "Asignar Tutorado",
        "GUIAssignTutoredModal",
        onClose
      ),
      tutoredDTO
    );
  }
}
