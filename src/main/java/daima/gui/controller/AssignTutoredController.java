package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dto.StaffDTO;
import daima.business.dao.TutoredDAO;
import daima.business.dto.TutoredDTO;
import daima.business.service.StaffService;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.ArrayList;

public class AssignTutoredController extends Controller {
  @FXML
  private Label title;
  @FXML
  private TextField fieldTutored;
  @FXML
  private ComboBox<StaffDTO> fieldTutor;
  @FXML
  private Label labelTagTutor;
  private TutoredDTO editTutoredDTO;

  public void setContext(ArrayList<StaffDTO> tutorDTOList, TutoredDTO editTutoredDTO) {
    this.editTutoredDTO = editTutoredDTO;
    configureFieldTutor(tutorDTOList);
    configureUI();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureFieldTutor(ArrayList<StaffDTO> tutorDTOList) {
    fieldTutor.getItems().setAll(tutorDTOList);
  }

  private void configureUI() {
    configureTitle();
    configureFormData();
  }

  private void configureTitle() {
    if (editTutoredDTO.getIDTutor().isPresent()) {
      title.setText("Reasignar Tutorado");
    } else {
      title.setText("Asignar Tutorado");
    }
  }

  private void configureFormData() {
    if (editTutoredDTO == null) return;

    fieldTutored.setText(editTutoredDTO.toString());
    applyFieldTutoredSelection();
  }

  private void applyFieldTutoredSelection() {
    if (editTutoredDTO.getIDTutor().isPresent()) {
      for (StaffDTO tutorDTO : fieldTutor.getItems()) {
        if (tutorDTO.getID() == editTutoredDTO.getIDTutor().get()) {
          fieldTutor.setValue(tutorDTO);
          break;
        }
      }
    }
  }

  private void cleanErrorLabels() {
    labelTagTutor.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    if (fieldTutor.getValue() == null) {
      labelTagTutor.setText("Por favor, seleccione un tutor para el tutorado.");
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private void assignTutored() throws UserDisplayableException {
    editTutoredDTO.setIDTutor(fieldTutor.getValue().getID());
    TutoredDAO.getInstance().updateOne(editTutoredDTO);
    AlertFacade.showSuccessAndWait("El tutorado ha sido asignado exitosamente.");
    close();
  }

  private void reassignTutored() throws UserDisplayableException {
    int idTutor = editTutoredDTO.getIDTutor().get();
    int newIdTutor = fieldTutor.getValue().getID();

    if (idTutor == newIdTutor) {
      AlertFacade.showInformationAndWait("El tutor seleccionado es el mismo que el actual.");
      return;
    }

    boolean shallUpdate = AlertFacade.showConfirmationAndWait(
      String.format(
        "¿Está seguro que desea reasignar el tutor del tutorado de %s a %s?",
        editTutoredDTO.getTutorName().get(),
        fieldTutor.getValue().getFullName()
      )
    );

    if (shallUpdate) {
      editTutoredDTO.setIDTutor(newIdTutor);
      TutoredDAO.getInstance().updateOne(editTutoredDTO);
      AlertFacade.showSuccessAndWait("El tutorado ha sido reasignado exitosamente.");
      close();
    }
  }

  public void onClickAssignTutored() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      if (editTutoredDTO.getIDTutor().isPresent()) {
        reassignTutored();
      } else {
        assignTutored();
      }
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  public static void displayAssignTutoredModal(TutoredDTO tutoredDTO, Runnable onClose) {
    try {
      ArrayList<StaffDTO> tutorDTOList = StaffService.getInstance().getAllTutorByProgramForRegistration(
        tutoredDTO.getIDProgram()
      );

      AssignTutoredController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Gestionar Asignación de Tutorado",
          "GUIAssignTutoredModal",
          onClose
        )
      );

      controller.setContext(tutorDTOList, tutoredDTO);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }
}
